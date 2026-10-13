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
    path: 'systems',
    title: 'Sistemas de RPG',
    loadComponent: () => import('./features/system/pages/system-list/system-list').then(m => m.SystemList)
  },
  {
    path: 'systems/new',
    title: 'Novo Sistema',
    loadComponent: () => import('./features/system/pages/create-system/create-system').then(m => m.CreateSystem)
  },
  {
    path: 'systems/:id',
    title: 'Editar Sistema',
    loadComponent: () => import('./features/system/pages/edit-system/edit-system').then(m => m.EditSystem)
  },
  {
    // Rota antiga mantida por compatibilidade com links existentes (WIP: Atualizar links depois).
    path: 'create-system',
    redirectTo: 'systems/new',
    pathMatch: 'full'
  }
];
