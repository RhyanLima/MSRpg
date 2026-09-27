import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomePlayerSharedSystems } from './home-player-shared-systems';

describe('HomePlayerSharedSystems', () => {
  let component: HomePlayerSharedSystems;
  let fixture: ComponentFixture<HomePlayerSharedSystems>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomePlayerSharedSystems],
    }).compileComponents();

    fixture = TestBed.createComponent(HomePlayerSharedSystems);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
