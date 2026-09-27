import { TestBed } from '@angular/core/testing';

import { RpgSystemService } from './rpg-system.service';

describe('RpgSystemService', () => {
  let service: RpgSystemService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RpgSystemService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
