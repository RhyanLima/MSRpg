import { ChangeDetectionStrategy, Component, inject, input, output } from '@angular/core';
import { AuthService } from '../../../auth/services/auth-service';

export type AdminView =
  | 'dashboard'
  | 'users'
  | 'sessions'
  | 'systems'
  | 'audit'
  | 'security';
 
interface NavItem {
  readonly view: AdminView;
  readonly icon: string;
  readonly label: string;
  readonly section: 'overview' | 'platform' | 'security';
}
 
const NAV_ITEMS: NavItem[] = [
  { view: 'dashboard', icon: '🏛',  label: 'Dashboard',        section: 'overview'  },
  { view: 'users',     icon: '👤',  label: 'Usuários',         section: 'platform'  },
  { view: 'sessions',  icon: '⚔',   label: 'Sessões Ativas',   section: 'platform'  },
  { view: 'systems',   icon: '🌐',  label: 'Sistemas RPG',     section: 'platform'  },
  { view: 'audit',     icon: '📜',  label: 'Audit Logs',       section: 'security'  },
  { view: 'security',  icon: '🔒',  label: 'Logs de Segurança',section: 'security'  },
];

@Component({
  selector: 'app-admin-sidebar',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [],
  templateUrl: './admin-sidebar.html',
  styleUrl: './admin-sidebar.scss',
})
export class AdminSidebar {

  protected readonly authService = inject(AuthService);
 
  readonly activeView = input.required<AdminView>();
 
  readonly viewChange = output<AdminView>();
  readonly logoutClick = output<void>();
 
  protected readonly navItems = NAV_ITEMS;
 
  protected readonly overviewItems = NAV_ITEMS.filter(i => i.section === 'overview');
  protected readonly platformItems = NAV_ITEMS.filter(i => i.section === 'platform');
  protected readonly securityItems = NAV_ITEMS.filter(i => i.section === 'security');
 
  protected selectView(view: AdminView): void {
    this.viewChange.emit(view);
  }

}
