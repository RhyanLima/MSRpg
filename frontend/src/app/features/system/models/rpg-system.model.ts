export interface RpgSystemSummary {
  readonly id: string;
  readonly name: string;
  readonly description?: string;
  readonly engineVersion: string;
  readonly contentVersion: string;
  readonly syncPolicy: string; // Politica de sincronia não precisa ficar no summary nem no RpgSystem solto, pode fazer parte do RpgSystemSettings, ajustar depois.
  readonly createdAt: string;
  readonly updatedAt: string;
}

export interface RpgSystem {
    // WIP: Representação completa do sistem conforme RpgSystem.java no backend
}


