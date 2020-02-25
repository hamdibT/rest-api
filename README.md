# REST API 

Pour builder le projet il faut lancer la commande **maven**:

```bash
mvn clean install
```

Pour lancer un build sans tests :

```bash
mvn clean install -DskipTests
```

Le standalone contient une base embarquée **H2** et doit pointer sur un fichier db exemple: test.mv.db
Aussi une interface SWAGGER a été rajouté et consultable via l'url :

```html
http://localhost:8087/swagger-ui.html
```
Les Models ainsi que les Controllers ont été documentés de telle sorte qu'il soit exploitable par des QA.

le jar resultant du build démarre sur le port 8087.

Pour lancer le jar il faut lancer cette commande:
 ```bash
 java -jar rest-api-0.0.1-SNAPSHOT.jar
 ```

Tous les paramètres de l'application.properties sont modifiables :

- en pointant sur un nouveau fichier de configuration 

 ```bash
 java -jar rest-api-0.0.1-SNAPSHOT.jar --spring.config.location=file:$$file_location
 ```
- en modifiant directement une configuration spécifique : l'exemple suivant modifie le path du fichier BD de H2

 ```bash
 java -jar rest-api-0.0.1-SNAPSHOT.jar -Dspring.datasource.url=jdbc:h2:file:D:/test
 ```
