# Как залить Duplo TD в GitHub

Ниже самый простой путь через `GitHub Desktop`.

## 1. Что уже подготовлено

В проект уже добавлен `.gitignore`.

Это значит, что в репозиторий не должны попасть:
- `build`
- `dist`
- `exports`
- `backups`
- `install_backups`
- `tools`
- локальные логи, zip-архивы и временные файлы

То есть в GitHub пойдут в основном:
- `src`
- `scripts`
- `docs`
- `README.md`

## 2. Установить GitHub Desktop

Скачай и установи:
- `https://desktop.github.com/`

Потом войди в свой GitHub-аккаунт внутри приложения.

## 3. Создать приватный репозиторий на GitHub

1. Открой `https://github.com/`
2. Нажми `New repository`
3. Назови его, например:
   `duplo-td`
4. Выбери `Private`
5. Не ставь галочки:
   - `Add a README file`
   - `Add .gitignore`
   - `Choose a license`
6. Нажми `Create repository`

## 4. Подключить локальную папку

1. Открой `GitHub Desktop`
2. Нажми:
   `File -> Add local repository`
3. Укажи папку:
   `E:\Hytale mods\bank_defense_autogen`
4. Если приложение скажет, что это ещё не git-репозиторий:
   нажми `create a repository`

Заполни так:
- `Name`: `duplo-td`
- `Description`: `Source project for Duplo TD`
- `Local path`: `E:\Hytale mods\bank_defense_autogen`

И нажми `Create Repository`.

## 5. Проверить список файлов перед первым коммитом

После создания репозитория GitHub Desktop покажет список файлов.

Проверь, что там нет:
- `backups`
- `install_backups`
- `dist`
- `exports`
- `build`
- больших `.zip`

Если их нет, значит `.gitignore` работает правильно.

Если что-то лишнее всё же появилось, не коммить пока это, сначала напиши мне.

## 6. Сделать первый коммит

Снизу слева в `Summary` напиши:

`Initial source upload for Duplo TD 0.2.1`

Потом нажми:

`Commit to main`

## 7. Опубликовать репозиторий

После коммита сверху нажми:

`Publish repository`

Проверь настройки:
- имя репозитория правильное
- галочка `Keep this code private` включена

Потом нажми:

`Publish Repository`

## 8. Получить ссылку

После публикации откроется или появится ссылка вида:

`https://github.com/ТВОЙ_АККАУНТ/duplo-td`

Именно её можно вставлять в форму как `Link to Source Code`.

## 9. Что отправлять в форму

В форму вставляй ссылку на сам репозиторий, например:

`https://github.com/ТВОЙ_АККАУНТ/duplo-td`

Этого достаточно.

## 10. Если попросят доступ

Если репозиторий приватный и команда не сможет его открыть, GitHub обычно показывает это сразу.

Тогда есть 2 варианта:
- временно сделать репозиторий `Unlisted/Public`
- или пригласить нужный GitHub-аккаунт в collaborators

Обычно для таких форм приватной ссылки хватает, если они действительно смотрят её своей командой.
