import { ChangeDetectionStrategy, Component } from '@angular/core';

type EmberVariant = 'ember' | 'gold' | 'faint';

interface EmberSpark {
  readonly id: number;
  readonly left: string;
  readonly size: number;
  readonly dur: string;
  readonly delay: string;
  readonly drift: string;
  readonly variant: EmberVariant;
}

const EMBER_COUNT = 26;
const VARIANTS: readonly EmberVariant[] = ['ember', 'ember', 'gold', 'faint'];

function buildEmbers(): EmberSpark[] {
  return Array.from({ length: EMBER_COUNT }, (_, i) => ({
    id: i,
    left: `${(Math.random() * 100).toFixed(2)}%`,
    size: Number((2 + Math.random() * 3.5).toFixed(1)),
    dur: `${(7 + Math.random() * 9).toFixed(1)}s`,
    delay: `${(Math.random() * 10).toFixed(1)}s`,
    drift: `${Math.round((Math.random() - 0.5) * 90)}px`,
    variant: VARIANTS[Math.floor(Math.random() * VARIANTS.length)],
  }));
}

@Component({
  selector: 'app-ember-particles',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [],
  templateUrl: './ember-particles.html',
  styleUrl: './ember-particles.scss',
})
export class EmberParticles {
  protected readonly embers: EmberSpark[] = buildEmbers();

  protected sparkStyle(spark: EmberSpark): string {
    return `left:${spark.left};width:${spark.size}px;height:${spark.size}px;`
      + `--dur:${spark.dur};--delay:${spark.delay};--drift:${spark.drift};`;
  }
}
