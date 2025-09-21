# 📌 Sistema de Cadastro de Usuários, Projetos e Equipes

## 📖 Sobre o Projeto
Este projeto foi desenvolvido em **Java** com o objetivo de implementar um sistema simples de gerenciamento de **usuários, projetos e equipes**, seguindo requisitos previamente definidos.  
O foco principal é aplicar conceitos de **Programação Orientada a Objetos (POO)**, manipulação de **listas**, **validações** e boas práticas de modelagem.

---

## 🛠 Funcionalidades

### 👤 Cadastro de Usuários
- Armazena:
  - Nome completo, CPF, e-mail, cargo, login e senha.
- Perfis disponíveis:
  - **Administrador**, **Gerente**, **Colaborador**.
- Senhas armazenadas utilizando **hash** para maior segurança.

### 📂 Cadastro de Projetos
- Informações do projeto:
  - Nome, descrição, data de início, data de término prevista e status.
- Status possíveis:
  - **Planejado**, **Em andamento**, **Concluído**, **Cancelado**.
- Cada projeto possui obrigatoriamente um **gerente responsável**.

### 👥 Cadastro de Equipes
- Informações da equipe:
  - Nome, descrição e membros (usuários vinculados).
- Uma equipe pode estar associada a **vários projetos**.

---

## 🧩 Estrutura do Código
- `enum Perfil` → Define os tipos de usuário.
- `enum StatusProjeto` → Define os estados de um projeto.
- `class Usuario` → Representa cada usuário do sistema.
- `class Projeto` → Representa um projeto, incluindo gerente e status.
- `class Equipe` → Representa uma equipe e seus membros.
- `Main` → Contém a lógica principal do sistema e o menu de interação.

---

## 🚀 Como Executar
1. Compile o código:
   ```bash
   javac Main.java
