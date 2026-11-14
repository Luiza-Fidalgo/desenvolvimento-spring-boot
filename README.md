# Continuum Control

API para controle de agentes, eventos historicos, anomalias e missoes temporais. Projeto academico em Spring Boot.

## Tecnologias

- Java 17+ (testado no Mac com Java 21), Spring Boot 4.1.2 e Maven Wrapper.
- PostgreSQL: agentes, eventos, anomalias, missoes, autorizacoes e intervencoes (JPA).
- MongoDB: ocorrencias temporais com detalhes flexiveis.
- Redis: cache de 15 segundos do resumo operacional.
- Spring Security: HTTP Basic e perfis OPERADOR, AGENTE, SUPERVISOR e ADMINISTRADOR.

## Subir no computador

Necessario: Java 17 ou superior e Docker com Docker Compose.

```bash
docker compose up -d
./mvnw test
./mvnw spring-boot:run
```

A aplicacao utiliza a porta 8080 e os servicos locais nas portas 5433 (PostgreSQL), 27018 (MongoDB) e 6380 (Redis).

Se for a primeira execucao e quiser dados iniciais, inicie assim:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

O profile `dev` cria um agente, um evento e uma anomalia de exemplo **apenas se a respectiva tabela estiver vazia**. Ele nao apaga dados existentes e nao cria missoes automaticamente.

## Executar tambem a aplicacao no Docker

Pare a aplicacao que estiver rodando localmente em 8080 e execute:

```bash
docker compose --profile app up --build -d
docker compose --profile app logs -f app
```

A imagem usa Maven para gerar o JAR e Java para executa-lo. O modo `--profile app` evita iniciar duas aplicacoes ao usar somente `docker compose up -d` para os bancos. A imagem nao executa testes durante o build; rode `./mvnw test` separadamente.

## Credenciais para demonstracao local

- `operador` / `operador123`
- `AG-B8-01` / `agente123`
- `supervisor` / `supervisor123`
- `administrador` / `admin123`

**As senhas sao apenas para testes locais.** O HTTP Basic deve ser usado com HTTPS fora deste ambiente. Use variaveis de ambiente para alterar senhas e conexoes sem mudar o codigo.

## Testar a API rapidamente

```bash
curl -i http://localhost:8080/agentes
# 401: acesso sem login

curl -i -u operador:operador123 http://localhost:8080/agentes
# 200: consulta com perfil autenticado

curl -i -u operador:operador123 http://localhost:8080/operacional/estado
# 200: resumo operacional vindo do PostgreSQL ou Redis
```

### Intervencoes temporais (Bloco 12)

A missao precisa estar `EM_EXECUCAO` e o agente deve participar dela. Nos dados de demonstracao anteriores, missao `2` e agente `2` foram usados; **confira os IDs no seu banco antes de executar**.

```bash
curl -i -u AG-B8-01:agente123 -X POST http://localhost:8080/missoes/2/intervencoes \
  -H 'Content-Type: application/json' \
  -d '{
    "agenteId": 2,
    "acao": "Ajustar equipamento temporal",
    "justificativa": "Reduzir a distorcao detectada",
    "impactoEstimado": "CONTROLADO",
    "resultadoObservado": "Sinal estabilizado"
  }'
# 201: intervencao registrada

curl -i -u operador:operador123 http://localhost:8080/missoes/2/intervencoes
curl -i -u operador:operador123 http://localhost:8080/anomalias/1/intervencoes
curl -i -u operador:operador123 http://localhost:8080/anomalias/1/historico
```

Tipos de impacto: MINIMO, CONTROLADO, SIGNIFICATIVO e SEVERO.

Se a missao nao estiver em execucao, a API responde 409. Um agente tentando agir como outro recebe 403.

## Endpoints principais

| Recurso | Operacoes |
|---|---|
| `/agentes` | Criar, listar, atualizar, buscar por filtros |
| `/eventos` | Criar, listar, buscar, filtrar, atualizar |
| `/anomalias` | Criar, listar, analisar, confirmar, atualizar |
| `/missoes` | Criar, autorizar, iniciar, concluir, falhar, consultar |
| `/missoes/{id}/ocorrencias` | POST, GET (MongoDB) |
| `/missoes/{id}/intervencoes` | POST, GET (PostgreSQL) |
| `/anomalias/{id}/historico` | GET (eventos agregados cronologicamente) |
| `/operacional/estado` | GET (indicador e cache Redis) |

Para ignorar o cache operacional: `/operacional/estado?atualizar=true`.

## Persistencia e consistencia

A operacao de iniciar/encerrar uma missao atualiza, numa transacao JPA, a missao, os agentes e, quando necessario, a anomalia. Isso evita alteracoes parciais entre essas tabelas do PostgreSQL. **A transacao JPA nao se estende automaticamente ao MongoDB ou ao Redis.**

Uma intervencao pertence a uma missao e a um agente (relacionamentos JPA). O historico agrega detecao, autorizacao, intervencoes, ocorrencias e encerramento, ordenados pelo instante. A implementacao atual **nao mantem um log completo de todas as alteracoes anteriores de risco ou situacao da anomalia, nem um instante persistido de criacao de todas as missoes**. Isso e uma limitacao que deve ser declarada na arguição.

No Redis fica apenas um cache chave-valor, com validade de 15 segundos. O banco oficial continua sendo o PostgreSQL. O indice de estabilidade foi definido para o projeto como `max(0, 100 - 8*A - 12*C - 3*E - 4*F)`, usando anomalias nao estabilizadas, criticas, missoes em execucao e falhas.

## Entrega

O `Dockerfile` permite construir a imagem da aplicacao; `docker-compose.yml` inicia a infraestrutura e, opcionalmente, a propria aplicacao. O `pom.xml` e o Maven Wrapper ficam no repositorio para construir o JAR fora da IDE. Verifique `./mvnw test` e `./mvnw clean package` antes de enviar.

O enunciado solicita **um PDF com o endereco do repositorio publico do GitHub**. O arquivo de submissao pode ser entregue separadamente da pasta do projeto.
