export type UsersQueryType = 'ALL' | 'CLIENTS' | 'EMPLOYEES' | 'WITHOUT_ROLE' | 'BANNED';

export type UserStatus = 'ACTIVE' | 'BANNED' | string;
export type UserRole = 'CLIENT' | 'EMPLOYEE' | string;
export type UserId = number | string;

export interface UserDto {
  id: UserId;
  name: string;
  email: string;
  status: UserStatus;
  registeredAt: string;
}

export interface CurrentUserDto extends UserDto {
  role: UserRole;
}
