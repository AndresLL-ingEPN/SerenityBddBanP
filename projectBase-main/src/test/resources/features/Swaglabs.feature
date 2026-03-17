@flujoCompleto
Feature: Pruebas de automatización web con Serenity BDD - Swaglabs

  @FlujoCompleto3
  Scenario: Iniciar sesión con credenciales válidas y completar compra de múltiples productos
    Given que el "cliente" abre el sitio web de pruebas para seleccionar el "Sauce Labs Backpack"
      | user          | password     |
      | standard_user | secret_sauce |
    And el cliente selecciona otro "Sauce Labs Bike Light"
    When el decide hacer la compra, ingresa sus datos personales
      | name  | lastname  | zip  |
      | andy  | ll        | 1733 |
    Then el realiza la compra del producto exitosamente

