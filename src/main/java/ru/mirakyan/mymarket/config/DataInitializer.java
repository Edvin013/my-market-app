package ru.mirakyan.mymarket.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.mirakyan.mymarket.model.Item;
import ru.mirakyan.mymarket.repository.ItemRepository;

@Configuration
@Profile("!test")
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(ItemRepository itemRepository) {
        return args -> {
            itemRepository.save(new Item(null, "Футбольный мяч", "Профессиональный футбольный мяч размер 5", "/images/ball.jpg", 2500L));
            itemRepository.save(new Item(null, "Баскетбольный мяч", "Баскетбольный мяч для игры в зале", "/images/basketball.jpg", 3000L));
            itemRepository.save(new Item(null, "Теннисная ракетка", "Профессиональная теннисная ракетка", "/images/racket.jpg", 8500L));
            itemRepository.save(new Item(null, "Беговые кроссовки", "Легкие кроссовки для бега", "/images/shoes.jpg", 5500L));
            itemRepository.save(new Item(null, "Фитнес-браслет", "Умный браслет с датчиком пульса", "/images/fitness-band.jpg", 3500L));
            itemRepository.save(new Item(null, "Йога-мат", "Нескользящий коврик для йоги", "/images/yoga-mat.jpg", 1500L));
            itemRepository.save(new Item(null, "Гантели 5 кг", "Набор гантелей по 5 кг", "/images/dumbbells.jpg", 2000L));
            itemRepository.save(new Item(null, "Скакалка", "Скакалка для кардио тренировок", "/images/rope.jpg", 500L));
            itemRepository.save(new Item(null, "Велосипед горный", "Горный велосипед 21 скорость", "/images/bike.jpg", 35000L));
            itemRepository.save(new Item(null, "Плавательные очки", "Очки для плавания с защитой UV", "/images/goggles.jpg", 1200L));
            itemRepository.save(new Item(null, "Рюкзак туристический", "Вместительный рюкзак 50 литров", "/images/backpack.jpg", 6500L));
            itemRepository.save(new Item(null, "Термос", "Термос из нержавеющей стали 1 литр", "/images/thermos.jpg", 1800L));
            itemRepository.save(new Item(null, "Спортивная бутылка", "Бутылка для воды 750 мл", "/images/bottle.jpg", 600L));
            itemRepository.save(new Item(null, "Компас", "Туристический компас с жидкостью", "/images/compass.jpg", 800L));
            itemRepository.save(new Item(null, "Палатка 2-местная", "Легкая палатка для похода", "/images/tent.jpg", 12000L));
        };
    }
}

