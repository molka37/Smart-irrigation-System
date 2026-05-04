#include <WiFi.h>
#include <PubSubClient.h>

// WiFi - remplace par ton réseau
const char* ssid = "HUAWEI-2.4G-Uv7H";
const char* password = "uRvNhd3a";

// MQTT - localhost de ton PC
const char* mqttServer = "192.168.1.19";
const int mqttPort = 1883;
const char* mqttUser = "molka";
const char* mqttPassword = "molka123";


String myString;

WiFiClient espClient;
PubSubClient client(espClient);

int state = 0;

// Valeurs simulées
float simulateTemperature() {
  return 20.0 + (random(0, 150) / 10.0); // 20.0 à 35.0 °C
}

float simulateHumidity() {
  return 40.0 + (random(0, 400) / 10.0); // 40.0 à 80.0 %
}

float simulateMoisture() {
  return 10.0 + (random(0, 600) / 10.0); // 10.0 à 70.0 %
}

void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print("Message reçu sur topic: ");
  Serial.println(topic);
  Serial.print("Message: ");
  for (int i = 0; i < length; i++) {
    Serial.print((char)payload[i]);
    if (i == 0) {
      state = (int)(payload[i] - '0');
    }
  }
  Serial.println();
  Serial.println("-----------------------");
}

void reconnect() {
  while (!client.connected()) {
    Serial.println("Connexion MQTT...");
    if (client.connect("ESP32Client", mqttUser, mqttPassword)) {
      Serial.println("Connecté au broker MQTT !");
      client.subscribe("pump");
    } else {
      Serial.print("Echec, état: ");
      Serial.println(client.state());
      delay(2000);
    }
  }
}

void setup() {
  Serial.begin(115200);
  randomSeed(analogRead(0));

  WiFi.begin(ssid, password);
  Serial.print("Connexion WiFi");
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("\nWiFi connecté ! IP: " + WiFi.localIP().toString());

  client.setServer(mqttServer, mqttPort);
  client.setCallback(callback);
}

void loop() {
  if (!client.connected()) {
    reconnect();
  }
  client.loop();

  // Valeurs simulées
  float temp = simulateTemperature();
  float humidity = simulateHumidity();
  float moisture = simulateMoisture();

  // Construction du message JSON
  myString = "{\"id\":\"esp32_sim\",\"temperature\":";
  myString += String(temp, 1);
  myString += ",\"humidity\":";
  myString += String(humidity, 1);
  myString += ",\"moisture\":";
  myString += String(moisture, 1);
  myString += "}";

  Serial.println("Envoi: " + myString);
  client.publish("Smartirrigation", myString.c_str());

  delay(30000); // envoie toutes les 30 secondes
}
