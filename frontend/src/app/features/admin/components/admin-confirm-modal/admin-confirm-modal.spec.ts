import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminConfirmModal } from './admin-confirm-modal';

describe('AdminConfirmModal', () => {
  let component: AdminConfirmModal;
  let fixture: ComponentFixture<AdminConfirmModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminConfirmModal],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminConfirmModal);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
