# Biblio

Aplicativo Android para gerenciamento de biblioteca pessoal. Organize seus livros por status de leitura, acompanhe o que esta lendo e mantenha uma lista de desejos — tudo sincronizado em tempo real com o Firebase.

## Funcionalidades

- **Login com Google** — Autenticacao via Firebase Auth com persistencia de sessao
- **Estante organizada por abas** — Todos, Desejados, Lendo e Lidos
- **CRUD de livros** — Cadastro, edicao, visualizacao e exclusao
- **Capa do livro** — Captura pela camera ou selecao da galeria
- **Avaliacao e resenha** — Nota por estrelas e texto livre (para livros lidos)
- **Sincronizacao em tempo real** — Alteracoes refletem instantaneamente via Firestore

## Tecnologias

| Categoria | Tecnologia |
|-----------|------------|
| Linguagem | Kotlin |
| UI | Material Design 3 |
| Autenticacao | Firebase Authentication (Google OAuth) |
| Banco de dados | Cloud Firestore |
| Imagens | Coil |
| Arquitetura | MVVM + Repository Pattern |
| Navegacao | ViewPager2 + TabLayout + FragmentTransaction |
| Reatividade | LiveData |

## Requisitos

- Android Studio Ladybug ou superior
- Min SDK 24 (Android 7.0)
- Conta Google configurada no dispositivo
- Projeto Firebase com Authentication e Firestore habilitados

## Configuracao

1. Clone o repositorio:
   ```bash
   git clone https://github.com/seu-usuario/biblio.git
   ```

2. Abra o projeto no Android Studio.

3. Configure o Firebase:
   - Crie um projeto no [Firebase Console](https://console.firebase.google.com/)
   - Ative o provider **Google** em Authentication
   - Ative o **Cloud Firestore**
   - Baixe o `google-services.json` e coloque em `app/`
   - Registre o SHA-1 do seu certificado de debug:
     ```bash
     ./gradlew signingReport
     ```

4. Execute o app em um emulador ou dispositivo fisico.

## Estrutura do Projeto

```
com.example.biblio/
├── LoginActivity.kt            # Tela de login com Google
├── MainActivity.kt             # Container principal (toolbar, tabs, FAB)
├── adapters/
│   ├── BookAdapter.kt          # Adapter do RecyclerView
│   └── LibraryAdapter.kt       # Adapter do ViewPager2
├── model/
│   └── Book.kt                 # Data class do livro
├── repository/
│   ├── BookRepository.kt       # CRUD de livros no Firestore
│   └── UserRepository.kt       # Upsert de perfil do usuario
└── ui/library/
    ├── BookViewModel.kt        # ViewModel (MVVM)
    ├── BookCardView.kt         # View customizada do card
    ├── BookDetailFragment.kt   # Detalhes do livro
    ├── AddBookFragment.kt      # Formulario de criacao/edicao
    ├── AllBooksTabFragment.kt  # Aba: todos os livros
    ├── ReadBooksTabFragment.kt # Aba: lidos
    ├── ReadingBooksTabFragment.kt # Aba: lendo
    └── WishedBooksTabFragment.kt  # Aba: desejados
```

## Modelo de Dados (Firestore)

```
users/{uid}
├── name, email, photoUrl
├── createdAt, updatedAt
└── books/{bookId}
    ├── title, author, pages, status
    ├── rating, coverUrl, synopsis, review
    └── completionDate
```

Cada usuario possui sua propria subcolecao de livros, garantindo isolamento completo dos dados.

## Navegacao

```
LoginActivity
  └── (auth) → MainActivity
                 ├── Abas (ViewPager2)
                 │     ├── Todos
                 │     ├── Desejados
                 │     ├── Lendo
                 │     └── Lidos
                 │           └── Card → BookDetailFragment
                 │                        ├── Editar → AddBookFragment
                 │                        └── Excluir → volta
                 ├── FAB → AddBookFragment
                 └── Avatar → Logout → LoginActivity
```

## Licenca

Este projeto foi desenvolvido para fins academicos.
