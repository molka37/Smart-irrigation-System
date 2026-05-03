#include <WiFi.h>
#include <PubSubClient.h>

#define soil_moisture_pin 34
#define Led_pin 2

// WiFi
const char* ssid = "wifi";
const char* password = "wifi";

// MQTT - ton PC local
const char* mqttServer = "192.168.1.19";
const int mqttPort = 1883;
const char* mqttUser = "molka";
const char* mqttPassword = "molka123";

String myString;
int state = 0;
WiFiClient espClient;
PubSubClient client(espClient);

void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print("Message arrived in topic: ");
  Serial.println(topic);
  for (int i = 0; i < length; i++) {
    Serial.print((char)payload[i]);
    if (i == 0) {
      state = (int)(payload[i]) - 48;
    }
  }
  Serial.println();
}

void reconnect() {
  while (!client.connected()) {
    Serial.println("Connecting to MQTT...");
    if (client.connect("ESP32Client", mqttUser, mqttPassword)) {
      Serial.println("connected");
      client.subscribe("pump");
    } else {
      Serial.print("failed, state: ");
      Serial.println(client.state());
      delay(2000);
    }
  }
}

void setup() {
  Serial.begin(115200);
  pinMode(Led_pin, OUTPUT);

  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.println("Connecting to WiFi...");
  }
  Serial.println("Connected to WiFi!");

  client.setServer(mqttServer, mqttPort);
  client.setCallback(callback);
  reconnect();
}

void loop() {
  if (!client.connected()) {
    reconnect();
  }
  client.loop();

  int raw = analogRead(soil_moisture_pin);
  float moisture = (raw / 40.95);

  float temp = 25.0;
  float humidity = 60.0;

  myString = "{'id':'test','temprature':";
  myString += String(temp);
  myString += ",'humidity':";
  myString += String(humidity);
  myString += ",'moisture':";
  myString += String(moisture);
  myString += "}";

  Serial.print("Moisture: ");
  Serial.print(moisture);
  Serial.println("%");
  Serial.println(myString);

  client.publish("Smartirrigation", myString.c_str());
  digitalWrite(Led_pin, state);

  delay(30000);
}