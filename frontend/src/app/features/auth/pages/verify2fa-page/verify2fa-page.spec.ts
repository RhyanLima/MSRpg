import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Verify2faPage } from './verify2fa-page';

describe('Verify2faPage', () => {
  let component: Verify2faPage;
  let fixture: ComponentFixture<Verify2faPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Verify2faPage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Verify2faPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
