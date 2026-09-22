import { TestBed } from '@angular/core/testing';

import { NavScroll } from './nav-scroll';

describe('NavScroll', () => {
  let service: NavScroll;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NavScroll);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
