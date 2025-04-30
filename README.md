# LocMaq - Sistema de Gestão de Locação de Máquinas.
📋 Descrição

O LocMaq é um sistema desenvolvido para gerenciar o processo de locação de máquinas e equipamentos, visando facilitar a rotina de empresas do setor de construção, operação e logística.
## 🚀 Funcionalidades 
- Usuários com papéis distintos: planejador, logística, gestor (admin)
- Login e autenticação com permissões específicas
- Cadastro e gerenciamento de clientes
- Cadastro de donos dos equipamentos
- Cadastro e controle de equipamentos, com vínculo ao cliente e ao dono
- Criação de contratos de locação entre cliente, equipamento e logística
- Emissão e gestão de boletins de medição para acompanhamento das locações
- Gestão de disponibilidade dos equipamentos
- Controle de fluxo automatizado para facilitar operações
  
## 💻 Tecnologias Utilizadas **
- Backend: Java 21, Spring Boot
- Banco de Dados: MySQL, JPA/Hibernate
- Frontend: HTML5, CSS3, JavaScript
- Gerenciamento: Maven
  
## 📂 Estrutura do Projeto (MVC - Model - View - Controller)

## ⚙️ Instalação e Uso

Pré-requisitos:
- Java 17+
- MySQL Server
- Maven
  
### Clone o repositório:
git clone https://github.com/mateusblm/locmaq.git

cd locmaq

### Configure o arquivo application.properties conforme suas credenciais MySQL:
properties

spring.datasource.url=jdbc:mysql://localhost:3306/locmaq

spring.datasource.username=SEU_USUARIO

spring.datasource.password=SUA_SENHA

spring.jpa.hibernate.ddl-auto=update

Crie o banco de dados no MySQL:

### No MySQL Workbench
CREATE DATABASE locmaq;

Construa e rode a aplicação:
### Rodar o projeto
- ./mvnw spring-boot:run
- Acesse no navegador:
- http://localhost:8080/
- Usuário padrão:
- Login: admin
- Senha: root

## Contribuição
Contribuições são bem-vindas! Sinta-se à vontade para abrir issues e pull requests. Para contribuir:

Faça um fork do projeto.

Crie uma branch para sua feature (git checkout -b feature/nova-feature).

Commit suas alterações (git commit -m 'Adiciona nova feature').

Faça o push para a branch (git push origin feature/nova-feature).

Abra um Pull Request.

### Contato
Para mais informações, entre em contato:

Nome: Mateus Burlamaqui Moreira

Email: mateusblm@outlook.com

Linkedin: www.linkedin.com/in/mateus-burlamaqui-moreira/ 
