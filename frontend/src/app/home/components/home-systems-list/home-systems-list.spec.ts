import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomeSystemsList } from './home-systems-list';

describe('HomeSystemsList', () => {
  let component: HomeSystemsList;
  let fixture: ComponentFixture<HomeSystemsList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomeSystemsList],
    }).compileComponents();

    fixture = TestBed.createComponent(HomeSystemsList);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
