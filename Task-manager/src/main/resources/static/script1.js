document.addEventListener('DOMContentLoaded', function () {
    const mainList1 = document.getElementById('mainList');
    const selectedList1 = document.getElementById('selectedList');
    const selectedItems1 = {};

    const mainList2 = document.getElementById('mainList_module_list');
    const selectedList2 = document.getElementById('selectedList_modules');
    const selectedItems2 = {};

    function handleListClick(event, mainList, selectedList, selectedItems) {
        if (event.target.tagName === 'LI') {
            const selectedItem = event.target;
            const itemId = selectedItem.dataset.id;

            if (!selectedItems[itemId]) {
                const clonedItem = selectedItem.cloneNode(true);
                selectedList.querySelector('div').appendChild(clonedItem);
                selectedItems[itemId] = true;
            } else {
                console.log("Этот элемент уже выбран!");
            }
        }
    }

    mainList1.addEventListener('click', function (event) {
        handleListClick(event, mainList1, selectedList1, selectedItems1);
    });

    selectedList1.addEventListener('click', function (event) {
        if (event.target.tagName === 'LI') {
            const selectedItem = event.target;
            const itemId = selectedItem.dataset.id;

            if (selectedItems1[itemId]) {
                selectedItem.remove();
                delete selectedItems1[itemId];
            }
        }
    });

    mainList2.addEventListener('click', function (event) {
        handleListClick(event, mainList2, selectedList2, selectedItems2);
    });

    selectedList2.addEventListener('click', function (event) {
        if (event.target.tagName === 'LI') {
            const selectedItem = event.target;
            const itemId = selectedItem.dataset.id;

            if (selectedItems2[itemId]) {
                selectedItem.remove();
                delete selectedItems2[itemId];
            }
        }
    });

    const form = document.getElementById('productForm');
    form.addEventListener('submit', function () {
        const hiddenField1 = document.getElementById('selectedTemplates');
        hiddenField1.value = JSON.stringify(Object.keys(selectedItems1));

        const hiddenField2 = document.getElementById('selectedModules');
        hiddenField2.value = JSON.stringify(Object.keys(selectedItems2));
    });
});
