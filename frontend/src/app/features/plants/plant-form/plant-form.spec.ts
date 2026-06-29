import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlantForm } from './plant-form';

describe('PlantForm', () => {
  let component: PlantForm;
  let fixture: ComponentFixture<PlantForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PlantForm],
    }).compileComponents();

    fixture = TestBed.createComponent(PlantForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
