# DUPLO Defense - полный гайд по новой карте

Ниже только рабочие команды без спорных аргументов. Используй именно их.

## 1. Очистка старой разметки

```text
/bankdefense reset
/bankdefense visualclear
/bankdefense routeclearall
/bankdefense slotclearall
/bankdefense clearspawna
/bankdefense clearspawnb
/bankdefense clearspawnc
/bankdefense clearbank
/bankdefense clearvault
/bankdefense clearplayerstart
/bankdefense clearcontrol
/bankdefense clearvendor
```

## 2. Построй геометрию

Рекомендуемая структура:

- 3 входа врагов
- 3 рукава
- 2 merge-зоны
- 1 финальное кольцо у дупла
- 32 обычных слота
- 8-10 trap-слотов
- 6 супер-слотов

Рекомендуемые размеры:

- карта примерно `384 x 320`
- дорожка `4-5` блоков
- обычный pad `3x3`
- trap pad `3x3`
- super pad `5x5`

## 3. Поставь маркеры

Встань на нужный блок и введи:

```text
/bankdefense markspawna
/bankdefense markspawnb
/bankdefense markspawnc
/bankdefense markbank
/bankdefense markvault
/bankdefense markplayerstart
/bankdefense markcontrol
/bankdefense markvendor
```

Что это значит:

- `markspawna` - вход A
- `markspawnb` - вход B
- `markspawnc` - вход C
- `markbank` - центр крепости / дупла
- `markvault` - ядро защиты
- `markplayerstart` - старт игрока
- `markcontrol` - NPC управления матчем
- `markvendor` - NPC дерева прогрессии

## 4. Поставь маршрут A

```text
/bankdefense routeadda
```

Полезное:

```text
/bankdefense routelista
/bankdefense routepopa
/bankdefense routecleara
```

## 5. Поставь маршрут B

```text
/bankdefense routeaddb
```

Полезное:

```text
/bankdefense routelistb
/bankdefense routepopb
/bankdefense routeclearb
```

## 6. Поставь маршрут C

```text
/bankdefense routeaddc
```

Полезное:

```text
/bankdefense routelistc
/bankdefense routepopc
/bankdefense routeclearc
```

## 7. Общие команды по маршрутам

```text
/bankdefense routelistall
/bankdefense routeclearall
```

Рекомендация:

- линия A: `6-10` точек
- линия B: `5-8` точек
- линия C: `5-8` точек

Ставь точки только на:

- поворотах
- choke points
- merge points
- входах в финальную зону

## 8. Поставь обычные слоты

```text
/bankdefense slotaddstandard
```

Цель:

- `32` обычных слота

Хорошее распределение:

- `12` на внешних линиях
- `10` в центре
- `10` у финального кольца

## 9. Поставь trap-слоты

```text
/bankdefense slotaddtrap
```

Цель:

- `8-10` trap-слотов

Хорошее распределение:

- `2-3` на внешних линиях
- `3-4` в зонах слияния
- `3` у финального кольца

## 10. Поставь супер-слоты

```text
/bankdefense slotaddsuper
```

Цель:

- `6` супер-слотов

Хорошее распределение:

- `2` в центре
- `2` перед финальной зоной
- `2` у дупла

## 11. Полезные команды по слотам

```text
/bankdefense slotlist
/bankdefense slotremove 3
/bankdefense slotclearstandard
/bankdefense slotcleartrap
/bankdefense slotclearsuper
/bankdefense slotclearall
```

`slotremove 3` удаляет ближайший слот в радиусе `3`.

## 12. Обнови служебные объекты и визуалы

```text
/bankdefense refresh
```

Дополнительно:

```text
/bankdefense visualclear
/bankdefense visualstatus
```

## 13. Проверка карты

```text
/bankdefense validate
/bankdefense status
```

Должно быть:

- 3 спавна
- 3 маршрута
- 32 обычных слота
- 8-10 trap-слотов
- 6 супер-слотов

## 14. Тестовый запуск

```text
/bankdefense reset
/bankdefense money add 5000
/bankdefense start
```

UI-команды:

```text
/bankdefense menu
/bankdefense progression
/bankdefense reward
```

## 15. Если надо строить башни через команды

```text
/bankdefense towers
/bankdefense build archer
/bankdefense build crossbow
/bankdefense build dart_thrower
/bankdefense build frost_totem
/bankdefense build shock_relay
/bankdefense build armor_drill
/bankdefense upgrade
/bankdefense sell
```

## 16. Минимальный чеклист

```text
/bankdefense markspawna
/bankdefense markspawnb
/bankdefense markspawnc
/bankdefense markbank
/bankdefense markvault
/bankdefense markplayerstart
/bankdefense markcontrol
/bankdefense markvendor
/bankdefense routeadda
/bankdefense routeaddb
/bankdefense routeaddc
/bankdefense slotaddstandard
/bankdefense slotaddtrap
/bankdefense slotaddsuper
/bankdefense refresh
/bankdefense validate
```

## 17. Чертёж

Смотри:

- `docs/duplo_multilane_block_plan.svg`
