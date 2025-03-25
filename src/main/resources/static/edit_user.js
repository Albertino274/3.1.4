
const modal = document.getElementById('actionsModal');
const modalForm = document.getElementById('modalWindowAction');
const submitButton = document.getElementById('submitButton');
const removeButton = document.getElementById('removeButton');

async function fillModalWindow(userId) {
    try {
        const response = await fetch(`/admin/users/${userId}`);
        if (!response.ok) {
            throw new Error(`Ошибка получения данных`);
        }
        const userData = await response.json();
        console.log(userData.firstName);
        console.log(userData.email);

        modalForm.querySelector('input[name="id"]').value = userData.id;
        modalForm.querySelector('#editFirstName').value = userData.firstName;
        modalForm.querySelector('#editLastName').value = userData.lastName;
        modalForm.querySelector('#editAge').value = userData.age;
        modalForm.querySelector('#editPhoneNumber').value = userData.phoneNumber;
        modalForm.querySelector('#editEmail').value = userData.email;

        const rolesSelect = modalForm.querySelector('#editRolesSelect');
        const allRoles = ['USER', 'ADMIN'];

        rolesSelect.innerHTML = '';

        allRoles.forEach(roleName => {
            const option = document.createElement('option');
            option.value = roleName;
            option.text = roleName;
            option.selected = userData.roles.includes(roleName);
            rolesSelect.appendChild(option);
        });

        const modalInstance = new bootstrap.Modal(modal);
        modalInstance.show();
    } catch (error) {
        console.error('Ошибка при получении данных:', error);

    }
}

async function sendEditDataToServer(data) {
    try {
        const response = await fetch('/admin/save_edit', {
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

function setupEditModal(userId) {
    fillModalWindow(userId);
    modal.querySelector('.modal-title').textContent = 'Edit user';
    submitButton.textContent = 'SUBMIT';
    submitButton.style.display = 'inline-block';
    removeButton.style.display = 'none';
    toggleFields(true);

    submitButton.removeEventListener('click', handleSave);

    submitButton.addEventListener('click', handleSave);

    async function handleSave(event) {
        event.preventDefault();

        const formData = new FormData(modalForm);
        const data = Object.fromEntries(formData.entries());

        data.id = parseInt(data.id, 10);
        data.age = parseInt(data.age, 10);

        delete data.roles;
        const rolesSelect = modalForm.querySelector('#editRolesSelect');
        data.roles = Array.from(rolesSelect.selectedOptions)
            .map(option => option.value);

        if (data.password !== data.passwordConfirm) {
            alert('Пароли не совпадают!');
            return;
        }

        if (!data.password || data.password.trim() === '') {
            delete data.password;
            delete data.passwordConfirm;
        } else if (data.password.length < 8) {
            alert('Минимальная длина пароля - 8 символов.');
            return;
        }

        console.log("Отправляемые данные:", data);
        const jsonData = JSON.stringify(data);
        console.log("JSON для отправки:", jsonData);

        try {
            const modalInstance = bootstrap.Modal.getInstance(modal);

            await sendEditDataToServer(data);
            modalInstance.hide();
            updateTableRow(data);
            showUserInfo();

        } catch (error) {
            console.error('Ошибка при сохранении данных:', error);
        }
    }
}

async function editUser(userId) {
    await setupEditModal(userId);
}

function toggleFields(editable) {
    const fields = modalForm.querySelectorAll('input, select');
    fields.forEach(field => {
        if (editable) {
            field.removeAttribute('readonly');
        } else {
            field.setAttribute('readonly', true);
        }
    })
}

function updateTableRow(updatedUser) {
    const row = document.querySelector(`#tableUsers tr[data-id="${updatedUser.id}"]`);
    if (row) {
        row.innerHTML = `
            <td>${updatedUser.id}</td>
            <td>${updatedUser.firstName}</td>
            <td>${updatedUser.lastName}</td>
            <td>${updatedUser.email}</td>
            <td>${updatedUser.phoneNumber}</td>
            <td>${updatedUser.age}</td>
            <td>${updatedUser.roles.join(', ')}</td>
            <td>
                <button class="btn btn-info" onclick="editUser(${updatedUser.id})">Edit</button>
                <button class="btn btn-danger" onclick="deleteUser(${updatedUser.id})">Delete</button>
            </td>
        `;
    }
}
