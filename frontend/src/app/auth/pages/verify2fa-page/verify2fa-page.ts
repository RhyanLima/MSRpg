import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { OtpPage } from '../otp-page/otp-page';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';
import { map } from 'rxjs';

@Component({
  selector: 'app-verify2fa-page',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [OtpPage],
  templateUrl: './verify2fa-page.html',
  styleUrl: './verify2fa-page.scss',
})
export class Verify2faPage {
  private readonly route = inject(ActivatedRoute);
 
  private readonly queryParams = toSignal(
    this.route.queryParamMap.pipe(map(p => p.get('email') ?? '')),
    { initialValue: '' },
  );
 
  readonly email = computed(() => this.queryParams() ?? '');
}
