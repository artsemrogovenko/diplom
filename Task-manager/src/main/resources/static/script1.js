document.addEventListener('DOMContentLoaded', function () {
    const mainList = document.getElementById('mainList');
    const selectedList = document.getElementById('selectedList');
    const selectedItems = {};

    mainList.addEventListener('click', function (event) {
        if (event.target.tagName === 'LI') {
            const selectedItem = event.target;
            const itemId = selectedItem.dataset.id;

            if (!selectedItems[itemId]) {
                const clonedItem = selectedItem.cloneNode(true);
                selectedList.querySelector('div').appendChild(clonedItem); // добавляем клонированный элемент внутрь списка внутри #selectedList
                selectedItems[itemId] = true;
            } else {
                console.log("Этот элемент уже выбран!");
            }
        }
    });

    selectedList.addEventListener('click', function (event) {
        if (event.target.tagName === 'LI') {
            const selectedItem = event.target;
            const itemId = selectedItem.dataset.id;

            if (selectedItems[itemId]) {
                selectedItem.remove();
                delete selectedItems[itemId];
            }
        }
    });

    const form = document.getElementById('productForm');
    form.addEventListener('submit', function () {
        const hiddenField = document.getElementById('selectedTemplates');
        hiddenField.value = JSON.stringify(Object.keys(selectedItems));
    });
});

