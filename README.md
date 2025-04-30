# LocMaq - Sistema de Gestão de Locação de Máquinas.
📋 Descrição
O LocMaq é um sistema desenvolvido para gerenciar o processo de locação de máquinas e equipamentos, visando facilitar a rotina de empresas do setor de construção, operação e logística.

🚀 Funcionalidades
Usuários com papéis distintos: planejador, logística, gestor (admin)
Login e autenticação com permissões específicas
Cadastro e gerenciamento de clientes
Cadastro de donos dos equipamentos
Cadastro e controle de equipamentos, com vínculo ao cliente e ao dono
Criação de contratos de locação entre cliente, equipamento e logística
Emissão e gestão de boletins de medição para acompanhamento das locações
Gestão de disponibilidade dos equipamentos
Controle de fluxo automatizado para facilitar operações
💻 Tecnologias Utilizadas
Backend: Java 21, Spring Boot
Banco de Dados: MySQL, JPA/Hibernate
Frontend: HTML5, CSS3, JavaScript
Gerenciamento: Maven
📂 Estrutura do Projeto
src/
  main/
    java/
      io.github.mateusbm.locmaq/
        models/
          Usuario.java
          TipoUsuario.java
          Cliente.java
          Dono.java
          Equipamento.java
          ContratoLocacao.java
          BoletimMedicao.java
          EquipamentoBoletimMedicao.java
        services/
        controllers/
    resources/
      application.properties
      static/
        css/
        js/
        index.html
⚙️ Instalação e Uso
Pré-requisitos:

Java 17+
MySQL Server
Maven
Clone o repositório:

bash
git clone https://github.com/mateusblm/locmaq.git
cd locmaq
Configure o arquivo application.properties conforme suas credenciais MySQL:

properties
spring.datasource.url=jdbc:mysql://localhost:3306/locmaq
spring.datasource.username=SEU_USUARIO
spring.datasource.password=SUA_SENHA
spring.jpa.hibernate.ddl-auto=update
Crie o banco de dados no MySQL:

sql
CREATE DATABASE locmaq;
Construa e rode a aplicação:

bash
./mvnw spring-boot:run
Acesse no navegador:

http://localhost:8080/
Usuário padrão:
Login: admin
Senha: root
