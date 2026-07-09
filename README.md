# SmartMatch Backend API

SmartMatch, Spring Boot 3.x, PostgreSQL, Redis ve Gemini API entegrasyonu kullanan, yapay zeka destekli bir iş ilanı ve aday eşleştirme platformudur.

## 🚀 3 Komutla Projeyi Ayağa Kaldırma

Projenin canlı (production) ortam altyapısını Docker üzerinde hızlıca başlatmak için aşağıdaki adımları sırasıyla takip edin:

### 1. Ortam Değişkenlerini Hazırlayın
Kök dizinde bulunan şablon dosyasından kendinize özel bir `.env` dosyası türetin ve gerekli API anahtarlarınızı girin:
```bash
cp .env.example .env