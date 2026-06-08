import { Component, OnInit, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ApiService } from './core/services/api.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App implements OnInit {
  
  loading = signal(true);
  error = signal<string | null>(null);
  health = signal<string>("");

  constructor(private readonly api: ApiService) {}
  
  async ngOnInit(): Promise<void> {
    try {
      await this.api.initialize();

      this.api.health().subscribe({
        next: response => {
          this.health.set(JSON.stringify(response, null, 2));
          this.loading.set(false);
        },
        error: error => {
          this.error.set(`Falha ao conectar ao backend: ${error.message}`);
          this.loading.set(false);
        }
      });

    } catch (error) {
      this.error.set(String(error));
      this.loading.set(false);
    }
  }
}
