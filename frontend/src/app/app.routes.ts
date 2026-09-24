import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./features/auth/pages/login-page/login-page').then(m => m.LoginPage)
  },
  {
    path: 'home',
    loadComponent: () => import('./features/home/home').then(m => m.Home)
  },
  {
    path: 'auth/login',
    loadComponent: () => import('./features/auth/pages/login-page/login-page').then(m => m.LoginPage)
  },
  {
    path: 'auth/register',
    loadComponent: () => import('./features/auth/pages/register-page/register-page').then(m => m.RegisterPage)
  },
  {
    path: 'auth/forgot-password',
    loadComponent: () => import('./features/auth/pages/forgot-password-page/forgot-password-page').then(m => m.ForgotPasswordPage)
  },
  {
    path: 'admin',
    loadComponent: () => import('./features/admin/pages/admin-dashboard/admin-dashboard').then(m => m.AdminDashboard)
  },
  {
    path: 'create-system',
    loadComponent: () => import('./features/system/create-system/create-system').then(m => m.CreateSystem)
  }
];
