import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { RpgSystemService, RpgSystemSummary } from '../core/services/rpg-system.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home implements OnInit {

private rpgSystemService = inject(RpgSystemService);

  systems = signal<RpgSystemSummary[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  ngOnInit(): void {
    this.loadSystems();
  }

  loadSystems() {
    this.loading.set(true);
    this.rpgSystemService.listSystems().subscribe({
      next: (data) => {
        this.systems.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('Falha ao carregar sistemas de RPG.');
        this.loading.set(false);
        console.error(err);
      }
    });
  }

}
