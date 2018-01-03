import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UserUpdatePassComponent } from './user-update-pass.component';

describe('UserUpdatePassComponent', () => {
  let component: UserUpdatePassComponent;
  let fixture: ComponentFixture<UserUpdatePassComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UserUpdatePassComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserUpdatePassComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
