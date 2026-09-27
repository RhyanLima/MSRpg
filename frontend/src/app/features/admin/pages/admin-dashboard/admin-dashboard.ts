import { CommonModule, DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { AccountType } from '../../../auth/auth.models';
import { AuthService } from '../../../auth/services/auth-service';
import { UserService } from '../../services/user-service';
import { UserResponse } from '../../user.models';
import { AdminConfirmModal } from '../../components/admin-confirm-modal/admin-confirm-modal';
import { AdminDistChart, DistItem } from '../../components/admin-dist-chart/admin-dist-chart';
import { AdminKpiGrid, KpiItem } from '../../components/admin-kpi-grid/admin-kpi-grid';
import { AdminSidebar, AdminView } from '../../components/admin-sidebar/admin-sidebar';
import { AdminUsersTable } from '../../components/admin-users-table/admin-users-table';

interface ModalState<T = null> {
  readonly open: boolean;
  readonly userId: string;
  readonly userName: string;
  readonly extra: T;
}

const CLOSED = <T>(extra: T): ModalState<T> =>
  ({ open: false, userId: '', userName: '', extra });

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, DatePipe, AdminSidebar, AdminKpiGrid, AdminUsersTable, AdminDistChart, AdminConfirmModal],
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.scss',
})
export class AdminDashboard implements OnInit {

  protected readonly authService = inject(AuthService);
  private readonly userService = inject(UserService);
  private readonly router = inject(Router);

  protected readonly now = new Date();

  protected readonly activeView = signal<AdminView>('dashboard');
  protected readonly users = signal<UserResponse[]>([]);
  protected readonly usersLoading = signal(false);
  protected readonly usersError = signal('');
  protected readonly actionLoading = signal(false);

  protected readonly promoteModal = signal(CLOSED<'ADMIN' | 'DEV' | null>(null));
  protected readonly deleteModal = signal(CLOSED<null>(null));

  private readonly adminCount = computed(() => this.users().filter(u => this.typeLabel(u) === 'ADMIN').length);
  private readonly devCount = computed(() => this.users().filter(u => this.typeLabel(u) === 'DEV').length);
  private readonly commonCount = computed(() => this.users().filter(u => this.typeLabel(u) === 'COMMON').length);

  protected readonly kpiItems = computed<KpiItem[]>(() => [
    { label: 'Total de Usuários', value: this.users().length, delta: 'registrados', icon: '👤', accent: 'gold' },
    { label: 'Admins', value: this.adminCount(), delta: 'TYPE_ADMIN + DEV', icon: '⚔', accent: 'ember' },
    { label: 'Devs', value: this.devCount(), delta: 'TYPE_DEV', icon: '🌐', accent: 'rune' },
    { label: 'Usuários Comuns', value: this.commonCount(), delta: 'TYPE_COMMON', icon: '📜', accent: 'green' },
  ]);

  protected readonly distItems = computed<DistItem[]>(() => {
    const total = this.users().length || 1;
    const pct = (n: number) => Math.round((n / total) * 100);
    return [
      { label: 'COMMON', count: this.commonCount(), percent: pct(this.commonCount()), type: 'common' },
      { label: 'ADMIN', count: this.adminCount(), percent: pct(this.adminCount()), type: 'admin' },
      { label: 'DEV', count: this.devCount(), percent: pct(this.devCount()), type: 'dev' },
    ];
  });

  protected readonly recentUsers = computed(() => this.users().slice(0, 6));

  ngOnInit(): void { this.loadUsers(); }

  protected setView(view: AdminView): void { this.activeView.set(view); }

  protected async onLogout(): Promise<void> { await this.authService.logout(); }

  protected openPromoteModal(user: UserResponse, role: 'ADMIN' | 'DEV'): void {
    this.promoteModal.set({ open: true, userId: user.id, userName: user.name, extra: role });
  }
  protected closePromoteModal(): void { this.promoteModal.set(CLOSED(null)); }

  protected async confirmPromotion(): Promise<void> {
    const { userId, extra: role } = this.promoteModal();
    if (!userId || !role) return;
    this.actionLoading.set(true);
    try {
      await firstValueFrom(role === 'DEV' ? this.userService.promoteToDev(userId) : this.userService.promoteToAdmin(userId));
      await this.loadUsers();
    } finally { this.actionLoading.set(false); this.closePromoteModal(); }
  }

  protected openDeleteModal(user: UserResponse): void {
    this.deleteModal.set({ open: true, userId: user.id, userName: user.name, extra: null });
  }
  protected closeDeleteModal(): void { this.deleteModal.set(CLOSED(null)); }

  protected async confirmDelete(): Promise<void> {
    const { userId } = this.deleteModal();
    if (!userId) return;
    this.actionLoading.set(true);
    try {
      await firstValueFrom(this.userService.delete(userId));
      await this.loadUsers();
    } finally { this.actionLoading.set(false); this.closeDeleteModal(); }
  }

  protected typeLabel(_user: UserResponse): AccountType {
    // TODO: remover quando UserResponse expor accountType
    return 'COMMON';
  }

  protected avatarGradient(name: string): string {
    const g = ['background:linear-gradient(135deg,#c0392b,#7b5ea7)', 'background:linear-gradient(135deg,#c9922a,#c0392b)', 'background:linear-gradient(135deg,#27ae60,#7b5ea7)', 'background:linear-gradient(135deg,#2980b9,#7b5ea7)', 'background:linear-gradient(135deg,#8e44ad,#c0392b)'];
    return g[name.charCodeAt(0) % g.length];
  }

  protected initials(name: string): string {
    return name.split(' ').slice(0, 2).map(n => n[0]?.toUpperCase() ?? '').join('');
  }

  private async loadUsers(): Promise<void> {
    this.usersLoading.set(true);
    this.usersError.set('');
    try {
      this.users.set(await firstValueFrom(this.userService.findAll()));
    } catch {
      this.usersError.set('Não foi possível carregar os usuários.');
    } finally { this.usersLoading.set(false); }
  }
}