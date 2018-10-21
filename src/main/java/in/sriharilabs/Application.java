/*
 * Copyright 2015-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package in.sriharilabs;

import com.microsoft.azure.spring.data.cosmosdb.core.query.DocumentDbPageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class Application implements CommandLineRunner{

    private static final String ID_1 = "id_1";
    private static final String NAME_1 = "myName_1";

    private static final String ID_2 = "id_2";
    private static final String NAME_2 = "myName_2";

    private static final String ID_3 = "id_3";
    private static final String NAME_3 = "myName_3";

    private static final String EMAIL = "xxx-xx@xxx.com";
    private static final String POSTAL_CODE = "0123456789";
    private static final String STREET = "zixing road";
    private static final String CITY = "shanghai";
    private static final String ROLE_CREATOR = "creator";
    private static final String ROLE_CONTRIBUTOR = "contributor";
    private static final int COST_CREATOR = 234;
    private static final int COST_CONTRIBUTOR = 666;
    private static final Long COUNT = 123L;

    private final User user_1 = new User(ID_1, EMAIL, NAME_1, COUNT);
    private final User user_2 = new User(ID_2, EMAIL, NAME_2, COUNT);
    private final User user_3 = new User(ID_3, EMAIL, NAME_3, COUNT);

    @Autowired
    private UserRepository userRepository;

    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
       printList(this.userRepository.findByEmailOrName(this.user_1.getEmail(), this.user_1.getName()));

        printList(this.userRepository.findByCount(COUNT, Sort.by(new Sort.Order(Sort.Direction.ASC, "count"))));

        printList(this.userRepository.findByNameIn(Arrays.asList(this.user_1.getName(), "fake-name")));

        queryByPageable();
    }

    private void queryByPageable() {
        final int pageSize = 2;
        final Pageable pageable = new DocumentDbPageRequest(0, pageSize, null);
        
        final Page<User> page = this.userRepository.findAll(pageable);
        System.out.println("***** Printing Page 1 *****");
        printList(page.getContent());

        
    }

    @PostConstruct
    public void setup() {
       // this.repository.save(user_1);
        //this.repository.save(user_2);
        this.userRepository.save(user_3);
    }

    @PreDestroy
    public void cleanup() {
        this.userRepository.deleteAll();
    }

    private void printList(List<User> users) {
        users.forEach(user -> System.out.println("this is working   "+user.getName()));
//    	for(User user:users) {
//    		System.out.println(user.toString());
//    	}
    }
}
