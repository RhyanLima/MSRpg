import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateSystem } from './create-system';

describe('CreateSystem', () => {
  let component: CreateSystem;
  let fixture: ComponentFixture<CreateSystem>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateSystem],
    }).compileComponents();

    fixture = TestBed.createComponent(CreateSystem);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
