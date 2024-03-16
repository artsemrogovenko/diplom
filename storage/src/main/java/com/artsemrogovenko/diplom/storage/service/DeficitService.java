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

import java.util.List;
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

    public void addToCard(String contractNumber, String userId, List<ComponentResponse> components) {
        for (ComponentResponse component : components) {

            Optional<Deficit> verify = deficitRepository.findByFactoryNumberAndModelAndNameAndUnitAndDescriptionAndRefill(
                    component.getFactoryNumber(),
                    component.getModel(),
                    component.getName(),
                    component.getUnit(),
                    component.getDescription(),
                    component.isRefill()
            );
            // если уже есть в базе такой компонент увеличиваю необходимое значение
            if (verify.isPresent()) {
                int quantity = verify.get().getQuantity();
                verify.get().setQuantity(quantity + (component.getQuantity() * (-1)));

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
                deficitComponent.setQuantity(component.getQuantity() * (-1));
                deficitComponent.setUnit(component.getUnit());
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
     * @param components элементы переданые при пополнении склада
     * @return
     */
    public void removeFromToCard(List<ComponentResponse> components) {
        for (ComponentResponse component : components) {

            Optional<Deficit> verify = deficitRepository.findByFactoryNumberAndModelAndNameAndUnitAndDescriptionAndRefill(
                    component.getFactoryNumber(),
                    component.getModel(),
                    component.getName(),
                    component.getUnit(),
                    component.getDescription(),
                    component.isRefill());
            //если такой элемент есть
            if (verify.isPresent()) {
                int quantity = verify.get().getQuantity();
                verify.get().setQuantity(quantity - component.getQuantity());
            }
        }
    }


}
