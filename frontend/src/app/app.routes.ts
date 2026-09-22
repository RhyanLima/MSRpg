import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./landing/pages/landing-page/landing-page').then(m => m.LandingPage)
  },
  {
    path: 'home',
    loadComponent: () => import('./home/home').then(m => m.Home)
  },
  {
    path: 'auth/login',
    loadComponent: () => import('./auth/pages/login-page/login-page').then(m => m.LoginPage)
  },
  {
    path: 'auth/register',
    loadComponent: () => import('./auth/pages/register-page/register-page').then(m => m.RegisterPage)
  },
  {
    path: 'auth/forgot-password',
    loadComponent: () => import('./auth/pages/forgot-password-page/forgot-password-page').then(m => m.ForgotPasswordPage)
  },
  {
    path: 'admin',
    loadComponent: () => import('./admin/pages/admin-dashboard/admin-dashboard').then(m => m.AdminDashboard)
  },
  {
    path: 'create-system',
    loadComponent: () => import('./create-system/create-system').then(m => m.CreateSystem)
  }
];
