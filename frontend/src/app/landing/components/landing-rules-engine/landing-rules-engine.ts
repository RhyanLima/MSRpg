import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RevealDirective } from '../../../core/directives/reveal-directive';


interface CodeToken {
  readonly type: 'key' | 'str' | 'val' | 'num' | 'comment' | 'brace' | 'plain';
  readonly text: string;
}

interface CodeLine {
  readonly tokens: CodeToken[];
}

@Component({
  selector: 'app-landing-rules-engine',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RevealDirective],
  templateUrl: './landing-rules-engine.html',
  styleUrl: './landing-rules-engine.scss',
})
export class LandingRulesEngine {

  readonly codeLines: CodeLine[] = [
    { tokens: [{ type: 'brace', text: '{' }] },
    {
      tokens: [
        { type: 'plain', text: '  ' },
        { type: 'key', text: '"id"' },
        { type: 'brace', text: ': ' },
        { type: 'str', text: '"rule_espada_fogo"' },
        { type: 'brace', text: ',' },
      ],
    },
    {
      tokens: [
        { type: 'plain', text: '  ' },
        { type: 'key', text: '"trigger"' },
        { type: 'brace', text: ': ' },
        { type: 'str', text: '"onHit"' },
        { type: 'brace', text: ',' },
      ],
    },
    {
      tokens: [
        { type: 'plain', text: '  ' },
        { type: 'comment', text: '// Condição opcional' },
      ],
    },
    {
      tokens: [
        { type: 'plain', text: '  ' },
        { type: 'key', text: '"condition"' },
        { type: 'brace', text: ': {' },
      ],
    },
    {
      tokens: [
        { type: 'plain', text: '    ' },
        { type: 'key', text: '"lhs"' },
        { type: 'brace', text: ': ' },
        { type: 'str', text: '"attacker.level"' },
        { type: 'brace', text: ',' },
      ],
    },
    {
      tokens: [
        { type: 'plain', text: '    ' },
        { type: 'key', text: '"op"' },
        { type: 'brace', text: ': ' },
        { type: 'str', text: '">="' },
        { type: 'brace', text: ', ' },
        { type: 'key', text: '"rhs"' },
        { type: 'brace', text: ': ' },
        { type: 'num', text: '3' },
      ],
    },
    { tokens: [{ type: 'plain', text: '  ' }, { type: 'brace', text: '},' }] },
    {
      tokens: [
        { type: 'plain', text: '  ' },
        { type: 'key', text: '"effects"' },
        { type: 'brace', text: ': [' },
      ],
    },
    { tokens: [{ type: 'plain', text: '    ' }, { type: 'brace', text: '{' }] },
    {
      tokens: [
        { type: 'plain', text: '      ' },
        { type: 'key', text: '"type"' },
        { type: 'brace', text: ': ' },
        { type: 'str', text: '"dice"' },
        { type: 'brace', text: ', ' },
        { type: 'key', text: '"expr"' },
        { type: 'brace', text: ': ' },
        { type: 'str', text: '"1d6+2"' },
        { type: 'brace', text: ',' },
      ],
    },
    {
      tokens: [
        { type: 'plain', text: '      ' },
        { type: 'key', text: '"target"' },
        { type: 'brace', text: ': ' },
        { type: 'str', text: '"vida"' },
        { type: 'brace', text: ', ' },
        { type: 'key', text: '"apply"' },
        { type: 'brace', text: ': ' },
        { type: 'str', text: '"subtract"' },
      ],
    },
    { tokens: [{ type: 'plain', text: '    ' }, { type: 'brace', text: '},' }] },
    { tokens: [{ type: 'plain', text: '    ' }, { type: 'brace', text: '{' }] },
    {
      tokens: [
        { type: 'plain', text: '      ' },
        { type: 'key', text: '"type"' },
        { type: 'brace', text: ': ' },
        { type: 'str', text: '"applyStatus"' },
        { type: 'brace', text: ',' },
      ],
    },
    {
      tokens: [
        { type: 'plain', text: '      ' },
        { type: 'key', text: '"statusId"' },
        { type: 'brace', text: ': ' },
        { type: 'val', text: '"status_burn"' },
        { type: 'brace', text: ', ' },
        { type: 'key', text: '"duration"' },
        { type: 'brace', text: ': ' },
        { type: 'num', text: '3' },
      ],
    },
    { tokens: [{ type: 'plain', text: '    ' }, { type: 'brace', text: '}' }] },
    { tokens: [{ type: 'plain', text: '  ' }, { type: 'brace', text: ']' }] },
    { tokens: [{ type: 'brace', text: '}' }] },
  ];

}
