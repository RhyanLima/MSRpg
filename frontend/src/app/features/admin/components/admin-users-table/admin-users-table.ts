import { ChangeDetectionStrategy, Component, computed, inject, input, output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AccountType } from '../../../auth/auth.models';
import { AuthService } from '../../../auth/services/auth-service';
import { UserResponse } from '../../user.models';
import { NgClass, SlicePipe } from '@angular/common';

@Component({
  selector: 'app-admin-users-table',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [FormsModule, SlicePipe, NgClass],
  templateUrl: './admin-users-table.html',
  styleUrl: './admin-users-table.scss',
})
export class AdminUsersTable {
  
  protected readonly authService = inject(AuthService);
 
  readonly users = input.required<UserResponse[]>();
  readonly loading = input(false);
 
  readonly promoteToAdmin = output<UserResponse>();
  readonly promoteToDev   = output<UserResponse>();
  readonly deleteUser     = output<UserResponse>();
 
  protected searchQuery = '';
  protected readonly activeFilter = signal<AccountType | 'ALL'>('ALL');
 
  protected readonly filters: ReadonlyArray<[AccountType | 'ALL', string]> = [
    ['ALL', 'Todos'],
    ['COMMON', 'COMMON'],
    ['ADMIN', 'ADMIN'],
    ['DEV', 'DEV'],
  ] as const;

  protected readonly filteredUsers = computed(() => {
    const filter = this.activeFilter();
    const query  = this.searchQuery.toLowerCase().trim();
    return this.users().filter(u => {
      const matchesType  = filter === 'ALL' || this.accountTypeLabel(u) === filter;
      const matchesQuery = !query || u.name.toLowerCase().includes(query) || u.email.toLowerCase().includes(query);
      return matchesType && matchesQuery;
    });
  });
 
  protected setFilter(filter: AccountType | 'ALL'): void {
    this.activeFilter.set(filter);
  }
 
  protected accountTypeLabel(_user: UserResponse): AccountType {
    // TODO: expor accountType no UserResponse quando a API incluir o campo
    return 'COMMON';
  }
 
  protected badgeClass(user: UserResponse): Record<string, boolean> {
    const type = this.accountTypeLabel(user);
    return { 'badge-common': type === 'COMMON', 'badge-admin': type === 'ADMIN', 'badge-dev': type === 'DEV' };
  }
 
  protected initials(name: string): string {
    return name.split(' ').slice(0, 2).map(n => n[0]?.toUpperCase() ?? '').join('');
  }
 
  protected avatarGradient(name: string): string {
    const gradients = [
      'background: linear-gradient(135deg, #c0392b, #7b5ea7)',
      'background: linear-gradient(135deg, #c9922a, #c0392b)',
      'background: linear-gradient(135deg, #27ae60, #7b5ea7)',
      'background: linear-gradient(135deg, #2980b9, #7b5ea7)',
      'background: linear-gradient(135deg, #8e44ad, #c0392b)',
    ];
    return gradients[name.charCodeAt(0) % gradients.length];
  }
}
