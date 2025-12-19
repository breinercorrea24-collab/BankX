## 🧪 Pruebas y Evidencias

Se siguió **al pie de la letra** la guía **LaboratorioBc68 – Sesión 08-09**, realizando **algunas correcciones menores** para asegurar el correcto funcionamiento de la aplicación.

### 📂 Carpeta: *Pruebas y Evidencias*

Dentro de esta carpeta se agregaron los siguientes elementos:

- 📬 **Colección de Postman**  
  Conjunto de requests utilizados para validar los endpoints del servicio.

- 🖼️ **Evidencias de ejecución en Postman**  
  Capturas que demuestran el consumo correcto de los endpoints y las respuestas esperadas.

---

## ✅ Pruebas Unitarias

- Se crearon **tests unitarios** para validar la lógica de negocio.
- Los tests aseguran el correcto comportamiento de los métodos principales.
- Se ejecutaron satisfactoriamente como parte del proceso de validación del proyecto.

---

📌 **Nota:**  
Las pruebas realizadas permiten verificar que la aplicación cumple con los requerimientos funcionales definidos en la guía del laboratorio.

## Test

mvn clean test 

## CheckStyle

mvn checkstyle:check

se genera : target/checkstyle-result.xml

## Sonar

bankx-transactions
sqp_d4968a55ed20ce4e057be36401148099c984d4fb


mvn -DskipTests=false \
 -Dsonar.projectKey=bankx-transactions \
 -Dsonar.projectName="BankX Transactions Service" \
 -Dsonar.host.url=http://localhost:9000 \
 -Dsonar.login=sqp_d4968a55ed20ce4e057be36401148099c984d4fb \
 -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \ 


 mvn -DskipTests=false -Dsonar.projectKey=bankx-transactions -Dsonar.projectName="BankX Transactions Service" -Dsonar.host.url=http://localhost:9000 -Dsonar.login=sqp_d4968a55ed20ce4e057be36401148099c984d4fb -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml 


mvn clean verify sonar:sonar ^
 -Dsonar.projectKey=bankx-transactions ^
 -Dsonar.projectName="BankX Transactions Service" ^
 -Dsonar.host.url=http://localhost:9000 ^
 -Dsonar.login=sqp_d4968a55ed20ce4e057be36401148099c984d4fb ^
 -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
