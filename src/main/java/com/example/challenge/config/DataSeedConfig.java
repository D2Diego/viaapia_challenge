package com.example.challenge.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.example.challenge.entity.Comment;
import com.example.challenge.entity.Incident;
import com.example.challenge.entity.IncidentPriority;
import com.example.challenge.entity.Role;
import com.example.challenge.entity.Status;
import com.example.challenge.entity.User;
import com.example.challenge.repository.CommentRepository;
import com.example.challenge.repository.IncidentRepository;
import com.example.challenge.repository.RoleRepository;
import com.example.challenge.repository.UsersRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Configuration
public class DataSeedConfig implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UsersRepository userRepository;
    private final IncidentRepository incidentRepository;
    private final CommentRepository commentRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public DataSeedConfig(RoleRepository roleRepository,
                           UsersRepository userRepository,
                           IncidentRepository incidentRepository,
                           CommentRepository commentRepository,
                           BCryptPasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.incidentRepository = incidentRepository;
        this.commentRepository = commentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        
        createUsers();
        createDemoIncidents();
    }

    private void createUsers() {
        var roleAdmin = roleRepository.findByName(Role.Values.ADMIN.name());
        var roleBasic = roleRepository.findByName(Role.Values.BASIC.name());

        createUserIfNotExists("admin", "123", Set.of(roleAdmin), "👤 Admin user");
        
        createUserIfNotExists("analista", "123", Set.of(roleBasic), "👨‍💼 Analista user");
        createUserIfNotExists("tecnico", "123", Set.of(roleBasic), "🔧 Técnico user");
        createUserIfNotExists("suporte", "123", Set.of(roleBasic), "📞 Suporte user");
    }

    private void createUserIfNotExists(String username, String password, Set<Role> roles, String description) {
        var existingUser = userRepository.findByUsername(username);
        
        existingUser.ifPresentOrElse(
            user -> {},
            () -> {
                var user = new User();
                user.setUsername(username);
                user.setPassword(passwordEncoder.encode(password));
                user.setRoles(roles);
                userRepository.save(user);
            }
        );
    }

    private void createDemoIncidents() {
        if (incidentRepository.count() > 0) {
            return;
        }


        List<Incident> demoIncidents = Arrays.asList(
            createIncident(
                "Servidor de produção não responde",
                "O servidor principal está apresentando timeout nas requisições. Usuários não conseguem acessar a aplicação.",
                IncidentPriority.HIGH,
                Status.OPEN,
                "admin@empresa.com",
                Arrays.asList("produção", "servidor", "crítico")
            ),
            createIncident(
                "Lentidão no banco de dados",
                "Consultas no banco de dados estão levando mais de 30 segundos para retornar resultados.",
                IncidentPriority.MEDIUM,
                Status.IN_PROGRESS,
                "analista@empresa.com",
                Arrays.asList("database", "performance", "lentidão")
            ),
            createIncident(
                "Falha no backup automático",
                "O backup automático da noite passada falhou com erro de espaço em disco insuficiente.",
                IncidentPriority.MEDIUM,
                Status.RESOLVED,
                "tecnico@empresa.com",
                Arrays.asList("backup", "storage", "automação")
            ),
            createIncident(
                "API de pagamentos instável",
                "A API de pagamentos está retornando erro 500 intermitentemente afetando as transações.",
                IncidentPriority.HIGH,
                Status.IN_PROGRESS,
                "suporte@empresa.com",
                Arrays.asList("api", "pagamentos", "transações")
            ),
            createIncident(
                "Problema de conectividade Wi-Fi",
                "Usuários do 3º andar relatando problemas de conectividade intermitente na rede Wi-Fi.",
                IncidentPriority.LOW,
                Status.OPEN,
                "tecnico@empresa.com",
                Arrays.asList("wifi", "rede", "conectividade")
            ),
            createIncident(
                "Certificado SSL expirado",
                "O certificado SSL do site principal expirou causando alertas de segurança nos navegadores.",
                IncidentPriority.HIGH,
                Status.RESOLVED,
                "admin@empresa.com",
                Arrays.asList("ssl", "certificado", "segurança")
            ),
            createIncident(
                "Falha no sistema de logs",
                "O sistema de coleta de logs parou de funcionar. Não temos visibilidade dos eventos das últimas 6 horas.",
                IncidentPriority.MEDIUM,
                Status.IN_PROGRESS,
                "analista@empresa.com",
                Arrays.asList("logs", "monitoramento", "observabilidade")
            )
        );

        List<Incident> savedIncidents = incidentRepository.saveAll(demoIncidents);

        createDemoComments(savedIncidents);
    }

    private Incident createIncident(String title, String description, IncidentPriority priority, 
                                   Status status, String responsibleEmail, List<String> tags) {
        var incident = new Incident();
        incident.setTitle(title);
        incident.setDescription(description);
        incident.setPriority(priority);
        incident.setStatus(status);
        incident.setResponsibleEmail(responsibleEmail);
        incident.setTags(tags);
        return incident;
    }

    private void createDemoComments(List<Incident> incidents) {
        if (incidents.size() >= 3) {
            createComment(incidents.get(0).getId(), "admin", 
                "Incident confirmado. Iniciando investigação dos logs do servidor.");
            createComment(incidents.get(0).getId(), "tecnico", 
                "Verificando status dos serviços e memória disponível.");
            
            createComment(incidents.get(1).getId(), "analista", 
                "Analisando queries mais lentas no banco. Identificadas 3 consultas problemáticas.");
            createComment(incidents.get(1).getId(), "admin", 
                "Necessário otimizar índices. Agendando manutenção para o final de semana.");
            
            createComment(incidents.get(2).getId(), "tecnico", 
                "Liberado espaço em disco e backup executado com sucesso.");
            createComment(incidents.get(2).getId(), "admin", 
                "Incident resolvido. Implementando monitoramento para evitar recorrência.");
        }        
    }

    private void createComment(java.util.UUID incidentId, String author, String message) {
        var comment = new Comment();
        comment.setIncidentId(incidentId);
        comment.setAuthor(author);
        comment.setMessage(message);
        commentRepository.save(comment);
    }
}