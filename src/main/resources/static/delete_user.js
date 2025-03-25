async function deleteUser(userId) {
    setupDeleteModal(userId);
}

function setupDeleteModal(userId) {
    fillModalWindow(userId);
    modal.querySelector('.modal-title').textContent = 'Delete user';
    submitButton.textContent = 'DELETE';
    submitButton.style.display = 'none';
    removeButton.style.display = 'inline-block';
    toggleFields(false);

    removeButton.removeEventListener('click', handleDelete);

    removeButton.addEventListener('click', handleDelete);

    async function handleDelete(event) {
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

            await sendDeleteDataToServer(data);
            modalInstance.hide();
            deleteTableRow(data);
            showUserInfo();

        } catch (error) {
            console.error('Ошибка при сохранении данных:', error);
        }
    }
}

async function sendDeleteDataToServer(data) {
    try {
        const response = await fetch('/admin/delete', {
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
        console.log('Удалено:', result);
    } catch (error) {
        console.error('Ошибка при удалении данных:', error);
    }
}

function deleteTableRow(deletedUser) {
    const row = document.querySelector(`#tableUsers tr[data-id="${deletedUser.id}"]`);
    if (row) {
        row.innerHTML = ``;
    }
}