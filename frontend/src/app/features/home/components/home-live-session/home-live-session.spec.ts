import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomeLiveSession } from './home-live-session';

describe('HomeLiveSession', () => {
  let component: HomeLiveSession;
  let fixture: ComponentFixture<HomeLiveSession>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomeLiveSession],
    }).compileComponents();

    fixture = TestBed.createComponent(HomeLiveSession);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
