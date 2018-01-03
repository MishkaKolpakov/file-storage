import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UserUpdateRoleComponent } from './user-update-role.component';

describe('UserUpdateRoleComponent', () => {
  let component: UserUpdateRoleComponent;
  let fixture: ComponentFixture<UserUpdateRoleComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UserUpdateRoleComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserUpdateRoleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
