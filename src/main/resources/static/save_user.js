document.addEventListener("DOMContentLoaded", function () {
    saveNewUser();
});

function saveNewUser() {
    const newUserForm = document.getElementById('add-user-form');
    const sendNewUserToServerBtn = document.getElementById('save-new-user-button');

    sendNewUserToServerBtn.addEventListener('click', async function (event){
        event.preventDefault();

        const newUserFormData = new FormData(newUserForm);
        const data = Object.fromEntries(newUserFormData.entries());

        delete data.roles;

        data.age = parseInt(data.age, 10);

        const rolesSelect = newUserForm.querySelector('#rolesSelect');
        data.roles = Array.from(rolesSelect.selectedOptions)
            .map(option => option.value);

        console.log(data.roles);

        if (data.password !== data.passwordConfirm) {
            alert('Пароли не совпадают!');
            return;
        }
        if (data.password.length < 8) {
            alert('Минимальная длина пароля - 8 символов.');
            return;
        }

        try {
            console.log("Отправляемые данные:", data);
            const jsonData = JSON.stringify(data);
            console.log("JSON для отправки:", jsonData);

            await sendNewUserToServer(data);

            refreshTable();

            const usersTab = document.getElementById('users-tab');
            const tabInstance = new bootstrap.Tab(usersTab);

            tabInstance.show();

        } catch (error) {
            console.error('Ошибка при сохранении данных:', error);
        }
    });
}

async function sendNewUserToServer(data) {
    try {
        const response = await fetch('/admin/save', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });
        if (!response.ok) {
            throw new Error(`Ошибка HTTP: ${response.status}`);
        }

        const result = await response.json();
        console.log('Сохранено:', result);
    } catch (error) {
        console.error('Ошибка при сохранении данных:', error);
    }
}

async function refreshTable() {
    try {
        const response = await fetch('/admin/users');
        if (!response.ok) {
            throw new Error(`Ошибка загрузки данных: ${response.status}`);
        }

        const users = await response.json();
        const tbody = document.querySelector('#tableUsers tbody');
        tbody.innerHTML = '';

        users.forEach((user, index) => {
            const row = document.createElement('tr');
            row.setAttribute('data-id', user.id);
            row.innerHTML = `
                <td>${index + 1}</td>
                <td>${user.firstName}</td>
                <td>${user.lastName}</td>
                <td>${user.email}</td>
                <td>${user.phoneNumber}</td>
                <td>${user.age}</td>
                <td>${user.roles.join(', ')}</td>
                <td>
                    <button class="btn btn-info" onclick="editUser(${user.id})">Edit</button>
                    <button class="btn btn-danger" onclick="deleteUser(${user.id})">Delete</button>
                </td>
            `;
            tbody.appendChild(row);
        });
    } catch (error) {
        console.error('Ошибка при обновлении таблицы:', error);
        alert('Не удалось обновить таблицу.');
    }
}