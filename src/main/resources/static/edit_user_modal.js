const modalManager = {
    data: null,
    saveData(button) {
        this.data = {
            id: button.getAttribute("data-id"),
            firstName: button.getAttribute("data-first-name"),
            lastName: button.getAttribute("data-last-name"),
            email: button.getAttribute("data-email"),
            phoneNumber: button.getAttribute("data-phone-number"),
            age: button.getAttribute("data-age"),
            roles: button.getAttribute("data-roles"),
        };
        localStorage.setItem('modalData', JSON.stringify(this.data));
    },
    getData() {
        const data = localStorage.getItem('modalData');
        return data ? JSON.parse(data) : null;
    }
};

document.addEventListener("DOMContentLoaded", function () {
    const error = document.getElementById('passwordsModal')
    const modalForm = document.getElementById('actionsModal');
    const editForm = modalForm.querySelector('form');

    if (error) {
        const modal = new bootstrap.Modal(modalForm);
        modal.show();

        const savedData = modalManager.getData();

        if (!savedData) {
            console.log("Данные не сохранились в modelManager");
        }

        editForm.querySelector('input[name="id"]').value = savedData.id;
        editForm.querySelector('#editFirstName').value = savedData.firstName;
        editForm.querySelector('#editLastName').value = savedData.lastName;
        editForm.querySelector('#editEmail').value = savedData.email;
        editForm.querySelector('#editPhoneNumber').value = savedData.phoneNumber;
        editForm.querySelector('#editAge').value = savedData.age;

        const rolesSelect = editForm.querySelector('#editRolesSelect');
        Array.from(rolesSelect.options).forEach(option => {
            option.selected = savedData.roles.split(",").map(Number).includes(Number(option.value));

            modalForm.querySelector('.modal-title').textContent = 'Edit user';
        });
    }

    modalForm.addEventListener('show.bs.modal', function (event) {
        const button = event.relatedTarget;

        if (button) {
            const action = button.getAttribute('data-bs-whatever');
            modalManager.saveData(button);

            const data = modalManager.getData();

            editForm.querySelector('input[name="id"]').value = data.id;
            editForm.querySelector('#editFirstName').value = data.firstName;
            editForm.querySelector('#editLastName').value = data.lastName;
            editForm.querySelector('#editEmail').value = data.email;
            editForm.querySelector('#editPhoneNumber').value = data.phoneNumber;
            editForm.querySelector('#editAge').value = data.age;

            const rolesSelect = editForm.querySelector('#editRolesSelect');

            Array.from(rolesSelect.options).forEach(option => {
                option.selected = data.roles.split(",").map(Number).includes(Number(option.value));
            });

            const submitButton = document.getElementById('submitButton');
            const removeButton = document.getElementById('removeButton');

            if (action === 'edit-button') {
                modalForm.querySelector('.modal-title').textContent = 'Edit user';
                submitButton.textContent = 'SUBMIT';
                submitButton.style.display = 'inline-block';
                removeButton.style.display = 'none';
                toggleFields(true);
            } else if (action === 'delete-button') {
                modalForm.querySelector('.modal-title').textContent = 'Delete user';
                submitButton.textContent = 'DELETE';
                submitButton.style.display = 'none';
                removeButton.style.display = 'inline-block';
                toggleFields(false);
            }
        }
    });
    document.getElementById('submitButton').addEventListener('click', function () {
        const form = document.getElementById('modalWindowAction');
        form.action = '/admin/save_edit';
        form.submit();
    });
    document.getElementById('removeButton').addEventListener('click', function () {
        const form = document.getElementById('modalWindowAction');
        form.action = '/admin/delete';
        form.submit();
    });

    function toggleFields(editable) {
        const fields = editForm.querySelectorAll('input, select');
        fields.forEach(field => {
            if (editable) {
                field.removeAttribute('readonly');
            } else {
                field.setAttribute('readonly', true);
            }
        })
    }
});