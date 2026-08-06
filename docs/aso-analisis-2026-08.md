# Análisis ASO — com.codergang.chatdirecto (agosto 2026)

Investigación hecha con 5 agentes: ficha en vivo, competidores EN, competidores ES/PT, mejores prácticas ASO 2025-2026, y síntesis.

## ⚠️ Discrepancia de marca detectada

- **Ficha en vivo en Play:** "Mensaji: Chat by Number" / "Mensaji: Chat por Número" (rebrand publicado en mayo 2026 directamente en Play Console).
- **Repo local:** `docs/play-listings.json` y `app_name` en strings.xml siguen diciendo **TapChat** — quien instala "Mensaji" ve "TapChat" en su launcher.
- Con solo 10+ instalaciones el costo de decidir la marca definitiva es casi cero, pero cada cambio de metadata tarda 7-21 días en re-indexar y el churn frecuente penaliza. **Decidir TapChat vs Mensaji y congelarlo debe ser el paso 0.** Sea cual sea, alinear ficha + `app_name` + JSON local.

## Estado real de la ficha (verificado en vivo)

- Live en en-US, es-419 y pt-BR. 10+ descargas. **Sin rating visible** (crítico para conversión). Categoría Productivity. Publicada mar 2026, actualizada may 2026. IAP $1.99–$39.99.
- Short descriptions en vivo: "Direct messages to any number without saving it to your contacts" (y traducciones).

## Diagnóstico del texto actual

- **EN:** "Chat by Number" no es la query dominante. Los líderes de 10M+ usan "Click to chat", "Without save number", "Direct Chat". 6 de los 10 títulos top usan "without saving / no contact". Se desperdician ~7 caracteres del título.
- **es-419:** "Chat por Número" es única en el nicho (nadie más la usa — vale conservarla en algún campo), pero falta "sin guardar/agregar contacto" que usan GUCA (4.79★, 49K reviews) y WhatsDirecto (10M+).
- **pt-BR:** el título es copia del español; no usa "Sem Salvar", "Sem Contato" ni la jerga "Zap". Es el título más débil de los tres.
- **Ninguna short description menciona WhatsApp**, y es el 2.º campo indexado. Todos los competidores top ponen "WhatsApp"/"WA" en la short description (solo lo evitan en el título). Tampoco mencionan plantillas, el diferenciador real de la app.

## Competidores clave

| App | Instalaciones | Rating | Estrategia de título |
|---|---|---|---|
| Click to chat (TrianguloY) | 10M+ | 4.6 (40K) | "Click to chat", sin marca; solo EN |
| DirectChat-Without save number / WhatsDirecto / WhatsDireto (Crazy Developers TK) | 10M+ | 4.6 | Único líder con títulos localizados ES/PT |
| Direct Chat & Messaging (Longnine) | 10M+ | 4.05 (75K) | package id relleno de keywords |
| Click to Chat - Without Saving / Chat Sin Agregar Contacto / ZAP Sem Contato (GUCA) | 1M+ | 4.79 (49K) | Hiper-localización: "Wasap", "Zap", Rappi/Pix/iFood en descripciones |
| Whatz Direct, Easy Message, Open Chat | 1M+ | 4.6-4.7 | "WA"/"Whatz" para esquivar la marca |

Patrón de marca: **ningún líder de 10M+ pone "WhatsApp" completo en el título** (solo apps de 10K-100K lo arriesgan), pero **todos lo usan libremente en las descripciones**. El riesgo real de takedown (vía AppDetex) es el logo/colores de WhatsApp en icono o feature graphic — la paleta propia indigo/coral actual elimina ese riesgo.

## Títulos propuestos (máx. 30 chars) — usar "TapChat" o "Mensaji" según la decisión de marca

**en-US**
| Opción | Chars | Racional |
|---|---|---|
| `TapChat: Chat Without Saving` | 28 | Keyword #1 del nicho (6/10 títulos top) — **recomendada** |
| `TapChat: Click to Chat No Save` | 30 | "click to chat" (4 títulos top) + "no save" |
| `TapChat: Chat with No Contact` | 29 | Variante "no contact" (Whatz Direct 1M+) |

**es-419**
| Opción | Chars | Racional |
|---|---|---|
| `TapChat: Sin Guardar Contacto` | 29 | Keyword de los dos líderes ES — **recomendada** |
| `TapChat: Chat sin Agendar` | 25 | "sin agendar" = fraseo LatAm sin competencia en títulos |
| `TapChat: Chat por Número` | 24 | Continuidad, keyword única actual |

**pt-BR**
| Opción | Chars | Racional |
|---|---|---|
| `TapChat: Zap Direto sem Salvar` | 30 | "zap direto" quedó vacante (2 apps removidas de Play) + "sem salvar" del líder — **recomendada, la mayor oportunidad de las tres locales** |
| `TapChat: Conversar sem Salvar` | 29 | Sin jerga, cubre "conversar sem salvar contato" |
| `TapChat: Zap sem Contato` | 24 | Colisiona con "ZAP Sem Contato" de GUCA — menos recomendable |

## Short descriptions propuestas (máx. 80 chars) — aquí SÍ va "WhatsApp"

- **en-US** (76): `Click to chat: message any number on WhatsApp without saving it to contacts.`
- **es-419** (78): `Chat de WhatsApp por número sin guardar contactos, con plantillas de mensajes.`
- **pt-BR** (77): `Mande mensagem no WhatsApp sem salvar o contato. Modelos de mensagem prontos.`

## Cambios puntuales a la descripción larga (no reescribir; densidad ~2-3%, no más)

- **EN:** insertar "click to chat" y "direct chat" 1 vez; "without saving contacts" 2-3 veces natural; "message templates" en PRO. Mencionar "WhatsApp Business" solo si la app realmente lo soporta.
- **es-419:** casos de uso locales estilo GUCA ("ventas por Marketplace, pedidos de Rappi o Uber Eats, compradores de Mercado Libre"); "sin agregar contacto" y "sin agendar el número" 1 vez; "plantillas de mensajes" 2 veces; una mención coloquial de "wasap".
- **pt-BR:** "sem adicionar contato" y "conversar sem salvar" 1 vez; "modelos de mensagem" 2 veces; casos locales ("comprovante de Pix, cliente do iFood, comprador da OLX ou Shopee"); "Zap" 1-2 veces natural; verificar que no lea como traducción automática.
- **PRIVACY (3 idiomas):** añadir el beneficio que venden los competidores: "si no guardas el número, el otro no ve tu foto de perfil ni tus estados".
- Conservar siempre el disclaimer de no afiliación.

## Palancas fuera del texto

1. **Rating visible = prioridad #1.** Implementar la **In-app Review API** disparada justo después de que un chat se abre con éxito. Reglas: sin pregunta previa ("¿te gusta la app?" está prohibido), sin conectarla a un botón (cuota ~1/mes por usuario, se gasta en silencio); para un botón "Califícanos" en Settings usar deep link a Play. Las ratings son por país: cada review de LatAm/BR mueve las estrellas en ese mercado. Meta: primeras ~20 ratings para que aparezcan estrellas.
2. **Responder todas las reviews** (reply rate >40% correlaciona con +0.4-0.8★).
3. **Screenshots localizados** (UI y captions en es/pt): flujo núcleo en los 2 primeros frames (número → chat abierto), plantillas en el 3.º. Mueven conversión 10-35% y la conversión-por-keyword alimenta el ranking. Usar Store Listing Experiments (A/B nativo gratis de Play Console), un elemento a la vez. Feature graphic sin verdes/logo de WhatsApp.
4. **Retención > velocity:** el algoritmo (update feb 2025) pesa retención D7/D30/D60 sobre velocidad de instalación, y prefiere goteo constante de reviews (10/semana sostenidas > ráfaga de 200).
5. **Localización futura:** es-ES casi gratis desde es-419; hi-IN/id-ID son mercados enormes del nicho pero durísimos. No prioritario ahora.
6. **Vitals:** crash/ANR bajo 8% por dispositivo es prerequisito de visibilidad.

## Priorización

| # | Acción | Cuándo | Impacto |
|---|---|---|---|
| 0 | Decidir marca (TapChat vs Mensaji), alinear app_name + ficha + JSON | Ya | Prerequisito |
| 1 | Títulos + short descriptions nuevos, los 3 idiomas en un solo release de metadata | Semana 1 | Fix de *gate*: hoy la app ni compite por "sin guardar contacto", "sem salvar", "without saving". Re-index 7-21 días; aparecerá en long-tail es/pt, no en head terms de 10M+ |
| 2 | Inserciones en descripción larga | Mismo release | Refuerzo semántico, gratis |
| 3 | In-app Review API + responder reviews | Semana 1-2 | El desbloqueador de conversión más grande disponible |
| 4 | Screenshots localizados + experimento de feature graphic | Semanas 2-4 | +10-35% conversión |
| 5 | No tocar metadata por 6-8 semanas; medir keywords de adquisición en Play Console | Continuo | La iteración 2 se decide con datos |

**Expectativa honesta:** con 10+ instalaciones y sin rating, ningún texto va a pelear "click to chat" en EN contra apps de 10M+. El camino viable: (a) ganar las long-tail vacantes de pt-BR ("zap direto", "sem adicionar contato") y es-419 ("chat por número", "plantillas de mensajes") donde no hay competencia en títulos, (b) construir rating visible y goteo de reviews en LatAm/BR, y (c) dejar que retención + conversión hagan el trabajo de posición. **pt-BR es el mercado con mejor relación esfuerzo/oportunidad.**

**Gaps de la investigación:** no se capturaron completas las descripciones largas es/pt en vivo; no hay datos de volumen de búsqueda reales (los "huecos" son ausencia de competidores en títulos, no volumen confirmado — validar con los reportes de adquisición de Play Console tras el re-index).
