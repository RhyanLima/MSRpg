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

  constructor(private readonly api: ApiService) { }

  async ngOnInit(): Promise<void> {
    try {
      await this.api.initialize();
    } catch (error) {
      this.error.set(String(error));
      this.loading.set(false);
    }
  }
}
