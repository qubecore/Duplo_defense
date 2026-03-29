# Гайд по расстановке DUO карты

Ниже только пошаговые команды.

Важно:
- на DUO карте сначала игроки выбирают сторону у NPC `Выбор стороны`;
- пока сторона не выбрана, остальные NPC на DUO карте подскажут в чат: `Сначала выбери сторону`;
- для одиночных тестов Duo можно временно включить `/bankdefense duotest on`;
- в DUO режиме контрактов нет;
- `Мастер Печати` больше не использует `seal node`-точки: теперь после смерти он вешает случайные дебаффы на следующие 5 волн.

## 1. Переключить мир в DUO

Команда:

```text
/bankdefense match duo
```

Что делает:
- включает DUO режим для этого мира;
- если ты один, всё равно позволяет собирать карту и тестировать её.

## 2. NPC на SOLO карте

### 2.1. QubeCore

Команда:

```text
/bankdefense markqubecoresolo
```

Что делает:
- ставит NPC `QubeCore` на SOLO карте.

Совместимость со старыми картами:

```text
/bankdefense markduo
```

Это старый алиас, он тоже ставит `QubeCore` на SOLO карте.

### 2.2. NPC телепорта на DUO карту

Встань там, где на SOLO карте должен стоять NPC телепорта.

Команда:

```text
/bankdefense markmode
```

Что делает:
- ставит NPC `Телепорт` на SOLO карте.

### 2.3. Куда телепортировать на SOLO карте

Встань в точку, куда игрок должен попадать при телепорте в SOLO.

Команда:

```text
/bankdefense markteleportsolo
```

Что делает:
- сохраняет точную точку телепорта на SOLO карте.

## 3. NPC на DUO карте

### 3.1. NPC выбора стороны

Встань там, где должен стоять NPC `Выбор стороны`.

Команда:

```text
/bankdefense markduoteam
```

Что делает:
- ставит NPC выбора стороны;
- при клике открывается UI выбора команды;
- после выбора обеих сторон остальные NPC станут доступны.

### 3.2. QubeCore на DUO карте

Встань там, где на DUO карте должен стоять `QubeCore`.

Команда:

```text
/bankdefense markqubecoreduo
```

Что делает:
- ставит отдельного NPC `QubeCore` именно на DUO карте.

### 3.3. NPC телепорта обратно

Встань там, где на DUO карте должен стоять NPC `Телепорт`.

Команда:

```text
/bankdefense markduoteleportnpc
```

Что делает:
- ставит NPC телепорта на DUO карте.

### 3.4. Куда телепортировать на DUO карте

Встань в точку, куда игрок должен попадать при телепорте в DUO.

Команда:

```text
/bankdefense markteleportduo
```

Что делает:
- сохраняет точную точку телепорта на DUO карте.

### 3.5. NPC статистики DUO

Встань там, где должен стоять NPC статистики DUO.

Команда:

```text
/bankdefense markduostats
```

### 3.6. NPC прокачки за осколки

Встань там, где должен стоять `Хранитель улучшений`.

Команда:

```text
/bankdefense markvendor
```

### 3.7. Оператор начала и конца матча

Встань там, где должен стоять `Оператор`.

Команда:

```text
/bankdefense markcontrol
```

## 4. Стартовые точки игроков

### 4.1. Синий игрок

Встань в стартовую точку синего игрока.

Команда:

```text
/bankdefense markduoplayerblue
```

### 4.2. Зелёный игрок

Встань в стартовую точку зелёного игрока.

Команда:

```text
/bankdefense markduoplayergreen
```

Если надо очистить оба старта и поставить заново:

```text
/bankdefense duoresetstarts
```

## 5. Спавны врагов

### 5.1. Синий маршрут

Встань в точку старта врагов синей линии.

Команда:

```text
/bankdefense markduospawnblue
```

### 5.2. Зелёный маршрут

Встань в точку старта врагов зелёной линии.

Команда:

```text
/bankdefense markduospawngreen
```

## 6. Центр карты и Hollow

### 6.1. Центр DUO карты

Встань в центр арены.

Команда:

```text
/bankdefense markduobank
```

### 6.2. Hollow

Встань в точку Hollow.

Команда:

```text
/bankdefense markduovault
```

## 7. Сбросить весь DUO маршрут и поставить заново

Команда:

```text
/bankdefense duoresetroutes
```

Что делает:
- очищает обе DUO линии;
- очищает `markduospawnblue`;
- очищает `markduospawngreen`.

После этого заново:

1. Встань на старт синего врага и введи:

```text
/bankdefense markduospawnblue
```

2. Иди по синему маршруту и на каждой важной точке вводи:

```text
/bankdefense duorouteaddblue
```

3. Встань на старт зелёного врага и введи:

```text
/bankdefense markduospawngreen
```

4. Иди по зелёному маршруту и на каждой важной точке вводи:

```text
/bankdefense duorouteaddgreen
```

Проверить маршруты:

```text
/bankdefense duoroutelistall
```

## 8. Сундуки

Встань в точку сундука.

Команда:

```text
/bankdefense duochestadd
```

Проверить список:

```text
/bankdefense duochestlist
```

## 9. Пады башен

### 9.1. Синий обычный пад

```text
/bankdefense slotaddblue
```

### 9.2. Зелёный обычный пад

```text
/bankdefense slotaddgreen
```

### 9.3. Синяя ловушка

```text
/bankdefense slotaddduotrapblue
```

### 9.4. Зелёная ловушка

```text
/bankdefense slotaddduotrapgreen
```

### 9.5. Общий super-pad

```text
/bankdefense slotaddduosuper
```

Если ошибся со слотом:

```text
/bankdefense slotremove
```

Если надо поменять команду уже существующего пада:

```text
/bankdefense slotteamblue
/bankdefense slotteamgreen
/bankdefense slotteamshared
```

## 10. Проверка

Команда:

```text
/bankdefense duostatus
```

Смотри на:
- `npcs`
- `starts`
- `spawns`
- `bank`
- `hollow`
- `teleportTargets`
- `routes`
- `slots`
- `validation`

## 11. Самый короткий порядок

1. `/bankdefense match duo`
2. `/bankdefense markduoteam`
3. `/bankdefense markqubecoreduo`
4. `/bankdefense duotest on` при необходимости тестировать Duo одному
5. `/bankdefense markduoteleportnpc`
6. `/bankdefense markteleportduo`
7. `/bankdefense markduostats`
8. `/bankdefense markvendor`
9. `/bankdefense markcontrol`
10. `/bankdefense markduoplayerblue`
11. `/bankdefense markduoplayergreen`
12. `/bankdefense markduospawnblue`
13. `/bankdefense markduospawngreen`
14. `/bankdefense markduobank`
15. `/bankdefense markduovault`
16. `/bankdefense duorouteaddblue`
17. `/bankdefense duorouteaddgreen`
18. `/bankdefense duochestadd`
19. `/bankdefense slotaddblue`
20. `/bankdefense slotaddgreen`
21. `/bankdefense slotaddduotrapblue`
22. `/bankdefense slotaddduotrapgreen`
23. `/bankdefense slotaddduosuper`
24. `/bankdefense duostatus`
