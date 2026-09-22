import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomeSidebar } from './home-sidebar';

describe('HomeSidebar', () => {
  let component: HomeSidebar;
  let fixture: ComponentFixture<HomeSidebar>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomeSidebar],
    }).compileComponents();

    fixture = TestBed.createComponent(HomeSidebar);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
