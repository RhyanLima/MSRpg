import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomeCalendar } from './home-calendar';

describe('HomeCalendar', () => {
  let component: HomeCalendar;
  let fixture: ComponentFixture<HomeCalendar>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomeCalendar],
    }).compileComponents();

    fixture = TestBed.createComponent(HomeCalendar);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
