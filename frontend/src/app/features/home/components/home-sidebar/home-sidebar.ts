import { ChangeDetectionStrategy, Component, computed, input, output } from '@angular/core';
import { HomeMode } from '../../home';

@Component({
  selector: 'app-home-sidebar',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [],
  templateUrl: './home-sidebar.html',
  styleUrl: './home-sidebar.scss',
})
export class HomeSidebar {

  readonly mode = input.required<HomeMode>();
  readonly sidebarOpen = input(false);

  readonly modeChange = output<'mestre' | 'jogador'>();
  readonly closeRequest = output<void>();
  readonly logoutClick = output<void>();

  protected readonly isMestre = computed(() => this.mode() === 'mestre');
  protected readonly userInitials = computed(() => 'A');
  protected readonly userName = computed(() => 'Aventureiro');
  protected readonly userEmail = computed(() => 'aventureiro@msrpg.com');

}
