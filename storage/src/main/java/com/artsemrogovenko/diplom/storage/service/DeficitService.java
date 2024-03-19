package com.artsemrogovenko.diplom.storage.service;

import com.artsemrogovenko.diplom.storage.dto.ComponentResponse;
import com.artsemrogovenko.diplom.storage.model.AccountName;
import com.artsemrogovenko.diplom.storage.model.ContractNumber;
import com.artsemrogovenko.diplom.storage.model.Deficit;
import com.artsemrogovenko.diplom.storage.repositories.AccountRepository;
import com.artsemrogovenko.diplom.storage.repositories.ContractNumberRepository;
import com.artsemrogovenko.diplom.storage.repositories.DeficitRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DeficitService {
    private final DeficitRepository deficitRepository;
    private final AccountRepository accountRepository;
    private final ContractNumberRepository contractNumberRepository;

    public List<Deficit> getAll() {
        return deficitRepository.findAll();
    }

    /**
     * Метод для добавления в закупки.
     *
     * @param contractNumber
     * @param userId
     * @param components     компоненты с отрицательным значением
     */

    public void addToCard(String contractNumber, Long taskId, String userId, List<ComponentResponse> components) {
        for (ComponentResponse component : components) {

            String unit = component.getUnit();
            Integer componentQuantity = component.getQuantity();

            if (component.getUnit().toLowerCase().equals("м")) {
                componentQuantity *= 1000;
                unit = "мм";
            }
            if (component.getUnit().toLowerCase().equals("км")) {
                componentQuantity *= 1000000;
                unit = "мм";
            }
            Optional<Deficit> verify = deficitRepository.findByFactoryNumberAndModelAndNameAndUnitAndDescriptionAndRefill(
                    component.getFactoryNumber(),
                    component.getModel(),
                    component.getName(),
                    unit,
                    component.getDescription(),
                    component.isRefill()
            );
            // если уже есть в базе такой компонент
            if (verify.isPresent()) {
                // если такой задачи не встречалось и номер заказа то добавлю в закупки
                if (!verify.get().getTaskIds().equals(taskId) && !verify.get().getContractNumbers().equals(contractNumber)) {

                    // увеличиваю на необходимое значение
                    int quantitySet = verify.get().getQuantity();
                    verify.get().setQuantity(quantitySet + (componentQuantity * (-1)));

                    ContractNumber number = new ContractNumber(contractNumber);
                    if (!contractNumberRepository.existsById(contractNumber)) {
                        contractNumberRepository.save(number);
                    }
                    verify.get().getContractNumbers().add(contractNumberRepository.getReferenceById(contractNumber));
                }

            } else {
                ContractNumber number = new ContractNumber(contractNumber);
                AccountName account = new AccountName(userId);
                if (!contractNumberRepository.existsById(contractNumber)) {
                    contractNumberRepository.save(number);
                }
                if (!accountRepository.existsById(userId)) {
                    accountRepository.save(account);
                }

                Deficit deficitComponent = new Deficit();

                deficitComponent.setFactoryNumber(component.getFactoryNumber());
                deficitComponent.setModel(component.getModel());
                deficitComponent.setName(component.getName());
                deficitComponent.setQuantity(componentQuantity * (-1));
                deficitComponent.setUnit(unit);
                deficitComponent.setDescription(component.getDescription());
                deficitComponent.setRefill(component.isRefill());

                deficitComponent.getAccountNames().add(accountRepository.getReferenceById(userId));
                deficitComponent.getContractNumbers().add(contractNumberRepository.getReferenceById(contractNumber));

                deficitRepository.save(deficitComponent);
            }
        }

    }

    /**
     * Метод для удаления из долгового списка
     *
     * @param components элементы переданные при пополнении склада
     * @return
     */
    public void removeFromToCard(List<ComponentResponse> components) {
        for (ComponentResponse component : components) {

            String unit = component.getUnit();
            Integer componentQuantity = component.getQuantity();

            if (component.getUnit().toLowerCase().equals("м")) {
                componentQuantity *= 1000;
                unit = "мм";
            }
            if (component.getUnit().toLowerCase().equals("км")) {
                componentQuantity *= 1000000;
                unit = "мм";
            }
            Optional<Deficit> verify = deficitRepository.findByFactoryNumberAndModelAndNameAndUnitAndDescriptionAndRefill(
                    component.getFactoryNumber(),
                    component.getModel(),
                    component.getName(),
                    unit,
                    component.getDescription(),
                    component.isRefill());
            //если такой элемент есть
            if (verify.isPresent()) {
                int quantity = verify.get().getQuantity();
                verify.get().setQuantity(quantity - componentQuantity);
            }
        }
    }


}
