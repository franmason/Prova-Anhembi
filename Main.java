import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Main {

    enum Perfil { ADMINISTRADOR, GERENTE, COLABORADOR }
    enum StatusProjeto { PLANEJADO, EM_ANDAMENTO, CONCLUIDO, CANCELADO }

    static class Usuario {
        int id;
        String nomeCompleto;
        String cpf;
        String email;
        String cargo;
        String login;
        String senha;
        Perfil perfil;

        Usuario(int id, String nomeCompleto, String cpf, String email, String cargo,
                String login, String senhaHash, Perfil perfil) {
            this.id = id;
            this.nomeCompleto = nomeCompleto;
            this.cpf = cpf;
            this.email = email;
            this.cargo = cargo;
            this.login = login;
            this.senha = senhaHash;
            this.perfil = perfil;
        }

        @Override
        public String toString() {
            String senhaMask = "********";
            return "#" + id + " | " + nomeCompleto + " | CPF: " + cpf +
                   " | email: " + email + " | cargo: " + cargo +
                   " | perfil: " + perfil + " | login: " + login +
                   " | senha: " + senhaMask;
        }
    }

    static class Projeto {
        int id;
        String nome;
        String descricao;
        LocalDate dataInicio;
        LocalDate dataTerminoPrevista;
        StatusProjeto status;
        int gerenteResponsavelId;

        Projeto(int id, String nome, String descricao, LocalDate dataInicio,
                LocalDate dataTerminoPrevista, StatusProjeto status, int gerenteResponsavelId) {
            this.id = id;
            this.nome = nome;
            this.descricao = descricao;
            this.dataInicio = dataInicio;
            this.dataTerminoPrevista = dataTerminoPrevista;
            this.status = status;
            this.gerenteResponsavelId = gerenteResponsavelId;
        }

        @Override
        public String toString() {
            return "#" + id + " | " + nome + " (" + status + ")" +
                    "\n    início: " + dataInicio +
                    " | término prev.: " + dataTerminoPrevista +
                    "\n    gerente id: " + gerenteResponsavelId +
                    "\n    desc: " + descricao;
        }
    }

    static class Equipe {
        int id;
        String nome;
        String descricao;
        List<Integer> membrosIds = new ArrayList<>();
        List<Integer> projetosIds = new ArrayList<>();

        Equipe(int id, String nome, String descricao) {
            this.id = id;
            this.nome = nome;
            this.descricao = descricao;
        }

        @Override
        public String toString() {
            StringBuilder membros = new StringBuilder();
            for (Integer uid : membrosIds) {
                Usuario u = Main.buscarUsuario(uid);
                if (u != null) {
                    membros.append(u.nomeCompleto).append(" (id ").append(uid).append("), ");
                }
            }
            if (membros.length() == 0) membros.append("nenhum");
            else membros.setLength(membros.length() - 2);

            StringBuilder projs = new StringBuilder();
            for (Integer pid : projetosIds) {
                Projeto p = Main.buscarProjeto(pid);
                if (p != null) {
                    projs.append(p.nome).append(" (id ").append(pid).append("), ");
                }
            }
            if (projs.length() == 0) projs.append("nenhum");
            else projs.setLength(projs.length() - 2);

            return "#" + id + " | " + nome +
                    "\n    desc: " + descricao +
                    "\n    membros: " + membros +
                    "\n    projetos: " + projs;
        }
    }

    static List<Usuario> usuarios = new ArrayList<>();
    static List<Projeto> projetos = new ArrayList<>();
    static List<Equipe> equipes = new ArrayList<>();
    static int seqUsuario = 1, seqProjeto = 1, seqEquipe = 1;

    static final Scanner sc = new Scanner(System.in);
    static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    static final int MIN_PASSWORD_LEN = 8;

    public static void main(String[] args) {
        System.out.println("=== Projeto desenvolvido por Francisco, Leonardo e Camila ===");
        boolean rodando = true;
        while (rodando) {
            menu();
            String op = sc.nextLine().trim();
            switch (op) {
                case "1" -> cadastrarUsuario();
                case "2" -> listarUsuarios();
                case "3" -> cadastrarProjeto();
                case "4" -> listarProjetos();
                case "5" -> cadastrarEquipe();
                case "6" -> listarEquipes();
                case "7" -> adicionarMembrosNaEquipe();
                case "8" -> vincularEquipeAProjeto();
                case "9" -> rodando = false;
                default -> System.out.println("opção inválida");
            }
            System.out.println();
        }
        System.out.println("saindo...");
    }

    static void menu() {
        System.out.println("\n--- MENU ---");
        System.out.println("1) Cadastrar usuário");
        System.out.println("2) Listar usuários");
        System.out.println("3) Cadastrar projeto");
        System.out.println("4) Listar projetos");
        System.out.println("5) Cadastrar equipe");
        System.out.println("6) Listar equipes");
        System.out.println("7) Adicionar membros em uma equipe");
        System.out.println("8) Vincular equipe a projeto");
        System.out.println("9) Sair");
        System.out.print("Escolha: ");
    }

    static void cadastrarUsuario() {
        System.out.println("\nCadastro de Usuário");
        System.out.print("Nome completo: ");
        String nome = sc.nextLine().trim();

        String cpf = pedirCPF();

        System.out.print("E-mail: ");
        String email = sc.nextLine().trim().toLowerCase(Locale.ROOT);

        System.out.print("Cargo: ");
        String cargo = sc.nextLine().trim();

        String login = pedirLoginUnico();

        String senhaHash = pedirSenhaSeguraHasheada();

        Perfil perfil = escolherPerfil();

        Usuario u = new Usuario(seqUsuario++, nome, cpf, email, cargo, login, senhaHash, perfil);
        usuarios.add(u);
        System.out.println("usuário cadastrado: " + u.id);
    }

    static String pedirCPF() {
        while (true) {
            System.out.print("CPF (11 dígitos): ");
            String cpf = sc.nextLine().replaceAll("\\D", "");
            if (cpf.length() != 11) {
                System.out.println("CPF inválido (use 11 dígitos).");
                continue;
            }
            if (cpfExiste(cpf)) {
                System.out.println("CPF já cadastrado. Digite outro.");
                continue;
            }
            return cpf;
        }
    }

    static String pedirLoginUnico() {
        while (true) {
            System.out.print("Login: ");
            String login = sc.nextLine().trim();
            if (login.isEmpty()) {
                System.out.println("login não pode ser vazio.");
                continue;
            }
            if (loginExiste(login)) {
                System.out.println("login já em uso. Tente outro.");
                continue;
            }
            return login;
        }
    }

    static String pedirSenhaSeguraHasheada() {
        while (true) {
            System.out.print("Senha (mínimo " + MIN_PASSWORD_LEN + " caracteres, com letra e número): ");
            String senha = sc.nextLine();
            if (!senhaValida(senha)) {
                System.out.println("senha fraca. Tente novamente.");
                continue;
            }
            return sha256Hex(senha);
        }
    }

    static boolean senhaValida(String s) {
        if (s == null || s.length() < MIN_PASSWORD_LEN) return false;
        boolean temLetra = false, temNumero = false;
        for (char c : s.toCharArray()) {
            if (Character.isLetter(c)) temLetra = true;
            if (Character.isDigit(c)) temNumero = true;
        }
        return temLetra && temNumero;
    }

    static String sha256Hex(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao hashear senha", e);
        }
    }

    static boolean cpfExiste(String cpf) {
        return usuarios.stream().anyMatch(u -> u.cpf.equals(cpf));
    }

    static boolean loginExiste(String login) {
        return usuarios.stream().anyMatch(u -> u.login.equalsIgnoreCase(login));
    }

    static Perfil escolherPerfil() {
        while (true) {
            System.out.print("Perfil [1-ADMIN, 2-GERENTE, 3-COLAB]: ");
            String p = sc.nextLine().trim();
            if (p.equals("1")) return Perfil.ADMINISTRADOR;
            if (p.equals("2")) return Perfil.GERENTE;
            if (p.equals("3")) return Perfil.COLABORADOR;
            System.out.println("opção inválida.");
        }
    }

    static void listarUsuarios() {
        if (usuarios.isEmpty()) {
            System.out.println("não há usuários.");
            return;
        }
        usuarios.forEach(u -> System.out.println(u.toString()));
    }

    static void cadastrarProjeto() {
        if (usuarios.stream().noneMatch(u -> u.perfil == Perfil.GERENTE)) {
            System.out.println("não dá pra criar projeto: cadastre pelo menos 1 usuário com perfil GERENTE.");
            return;
        }
        System.out.println("\nCadastro de Projeto");
        System.out.print("Nome do projeto: ");
        String nome = sc.nextLine().trim();
        System.out.print("Descrição: ");
        String desc = sc.nextLine().trim();
        LocalDate inicio = pedirData("Data de início (dd/MM/yyyy): ");
        LocalDate terminoPrev = pedirData("Data de término prevista (dd/MM/yyyy): ");
        StatusProjeto status = escolherStatus();

        System.out.println("Escolha o gerente responsável (apenas GERENTES):");
        listarSomenteGerentes();

        int gerenteId = pedirId("ID do gerente: ");
        Usuario gerente = buscarUsuario(gerenteId);
        if (gerente == null || gerente.perfil != Perfil.GERENTE) {
            System.out.println("gerente inválido (precisa ser perfil GERENTE).");
            return;
        }
        Projeto p = new Projeto(seqProjeto++, nome, desc, inicio, terminoPrev, status, gerenteId);
        projetos.add(p);
        System.out.println("projeto criado: " + p.id);
    }

    static StatusProjeto escolherStatus() {
        while (true) {
            System.out.print("Status [1-PLANEJADO, 2-EM_ANDAMENTO, 3-CONCLUIDO, 4-CANCELADO]: ");
            String s = sc.nextLine().trim();
            if (s.equals("1")) return StatusProjeto.PLANEJADO;
            if (s.equals("2")) return StatusProjeto.EM_ANDAMENTO;
            if (s.equals("3")) return StatusProjeto.CONCLUIDO;
            if (s.equals("4")) return StatusProjeto.CANCELADO;
            System.out.println("opção inválida.");
        }
    }

    static void listarProjetos() {
        if (projetos.isEmpty()) {
            System.out.println("não há projetos.");
            return;
        }
        projetos.forEach(p -> System.out.println(p.toString()));
    }

    static void cadastrarEquipe() {
        System.out.println("\nCadastro de Equipe");
        System.out.print("Nome da equipe: ");
        String nome = sc.nextLine().trim();
        System.out.print("Descrição: ");
        String desc = sc.nextLine().trim();
        Equipe e = new Equipe(seqEquipe++, nome, desc);
        equipes.add(e);
        System.out.println("equipe criada: " + e.id);
        if (!usuarios.isEmpty()) {
            System.out.println("Quer adicionar membros agora? (s/n)");
            String r = sc.nextLine().trim().toLowerCase(Locale.ROOT);
            if (r.equals("s")) adicionarMembrosNaEquipePorId(e.id);
        }
    }

    static void listarEquipes() {
        if (equipes.isEmpty()) {
            System.out.println("não há equipes.");
            return;
        }
        equipes.forEach(eq -> System.out.println(eq.toString()));
    }

    static void adicionarMembrosNaEquipe() {
        if (equipes.isEmpty()) {
            System.out.println("cadastre uma equipe antes.");
            return;
        }
        listarEquipes();
        int equipeId = pedirId("ID da equipe: ");
        adicionarMembrosNaEquipePorId(equipeId);
    }

    static void adicionarMembrosNaEquipePorId(int equipeId) {
        Equipe e = buscarEquipe(equipeId);
        if (e == null) {
            System.out.println("equipe não encontrada.");
            return;
        }
        if (usuarios.isEmpty()) {
            System.out.println("não há usuários para adicionar.");
            return;
        }
        System.out.println("Usuários disponíveis (digite IDs separados por vírgula, ou vazio pra parar):");
        listarUsuarios();
        System.out.print("IDs: ");
        String linha = sc.nextLine().trim();
        if (linha.isEmpty()) return;
        String[] partes = linha.split(",");
        for (String parte : partes) {
            try {
                int id = Integer.parseInt(parte.trim());
                if (buscarUsuario(id) != null && !e.membrosIds.contains(id)) {
                    e.membrosIds.add(id);
                }
            } catch (NumberFormatException ignored) {}
        }
        System.out.println("membros atualizados: " + e.membrosIds);
    }

    static void vincularEquipeAProjeto() {
        if (equipes.isEmpty() || projetos.isEmpty()) {
            System.out.println("precisa ter equipe e projeto cadastrados.");
            return;
        }
        listarEquipes();
        int equipeId = pedirId("ID da equipe: ");
        Equipe e = buscarEquipe(equipeId);
        if (e == null) { System.out.println("equipe não encontrada."); return; }
        listarProjetos();
        int projetoId = pedirId("ID do projeto: ");
        Projeto p = buscarProjeto(projetoId);
        if (p == null) { System.out.println("projeto não encontrado."); return; }
        if (!e.projetosIds.contains(projetoId)) e.projetosIds.add(projetoId);
        System.out.println("equipe #" + equipeId + " agora atua no projeto #" + projetoId);
    }

    static Usuario buscarUsuario(int id) {
        for (Usuario u : usuarios) if (u.id == id) return u;
        return null;
    }

    static Projeto buscarProjeto(int id) {
        for (Projeto p : projetos) if (p.id == id) return p;
        return null;
    }

    static Equipe buscarEquipe(int id) {
        for (Equipe e : equipes) if (e.id == id) return e;
        return null;
    }

    static int pedirId(String msg) {
        while (true) {
            System.out.print(msg);
            String s = sc.nextLine().trim();
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("digite um número inteiro.");
            }
        }
    }

    static LocalDate pedirData(String msg) {
        while (true) {
            System.out.print(msg);
            String s = sc.nextLine().trim();
            try {
                return LocalDate.parse(s, DF);
            } catch (DateTimeParseException e) {
                System.out.println("formato esperado: dd/MM/yyyy");
            }
        }
    }

    static List<Usuario> getGerentes() {
        List<Usuario> gs = new ArrayList<>();
        for (Usuario u : usuarios) if (u.perfil == Perfil.GERENTE) gs.add(u);
        return gs;
    }

    static void listarSomenteGerentes() {
        List<Usuario> gs = getGerentes();
        if (gs.isEmpty()) {
            System.out.println("não há usuários com perfil GERENTE.");
            return;
        }
        gs.forEach(u -> System.out.println("#" + u.id + " | " + u.nomeCompleto + " | login: " + u.login));
    }
}
