@flujoCompleto
Feature: Pruebas de automatización web con Serenity BDD - Swaglabs

  @FlujoCompletoBP
  Scenario Outline: Flujo completo de compra con múltiples productos usando datos externos del Excel
    Given que el "cliente" inicia sesion con "<user>" y "<password>" y selecciona el producto "<producto>"
    And el cliente agrega el segundo producto "<producto1>" al carrito
    When el cliente procede al checkout con nombre "<name>" apellido "<lastname>" y zip "<zip>"
    Then el cliente verifica que la compra fue realizada exitosamente

    Examples:
      | @externaldata@registro/dataDemoblaze.xlsx..compra |
