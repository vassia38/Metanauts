package com.main.controller;

import com.main.algo.Graph;
import com.main.model.Friendship;
import com.main.model.FriendshipDTO;
import com.main.model.Message;
import com.main.model.User;
import com.main.repository.RepositoryException;
import com.main.service.*;
import com.main.model.*;
import com.main.utils.events.*;
import com.main.utils.observer.Observer;
import com.main.utils.observer.OperationType;
import com.main.utils.observer.UpdateType;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ControllerClass implements Controller{
    private final UserService userService;
    private final  FriendshipService friendshipService;
    private final MessageService messageService;
    private final RequestService requestService;
    private final GroupService groupService;
    private final SocialEventService eventService;
    Graph graph;
    public ControllerClass(UserService userService, FriendshipService friendshipService,
                           MessageService messageService, RequestService requestService,
                           GroupService groupService, SocialEventService eventService){
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        this.requestService = requestService;
        this.groupService = groupService;
        this.eventService = eventService;
    }

    @Override
    public UserService getUserService(){
        return this.userService;
    }

    @Override
    public FriendshipService getFriendshipService() {
        return this.friendshipService;
    }

    @Override
    public MessageService getMessageService() {
        return this.messageService;
    }

    @Override
    public RequestService getRequestService() {
        return this.requestService;
    }

    @Override
    public GroupService getGroupService() {
        return this.groupService;
    }

    /**
     * add User(username,firstname,lastname)
     * @param entity user
     * @throws RepositoryException if user with same (id and ) username already exists
     */
    @Override
    public void addUser(User entity, String salt) {
        if(entity == null || entity.getUsername() == null ||
           entity.getUsername().equals(""))
            throw new IllegalArgumentException("Invalid user input info!\n");
        User user = userService.findOneByUsername(entity.getUsername());
        if(user != null)
            throw new RepositoryException("User already exists!\n");
        userService.add(entity, salt);
        user = userService.findOneByUsername(entity.getUsername());
        if(user != null)
            this.notifyObservers(UpdateType.USERS, new UserEvent(user, OperationType.ADD));
    }

    /**
     * delete logged-in user and its friendships
     * @param entity user
     * @return old state of entity
     * @throws RepositoryException if user was not found
     */
    @Override
    public User deleteUser(User entity) {
        User deleted = userService.delete(entity);
        if(deleted == null)
            throw new RepositoryException("User doesn't exist!\n");
        Iterable<Friendship> list = friendshipService.getAllEntities();
        for(Friendship fr : list){
            if (Objects.equals(fr.getId().getLeft(), entity.getId()) ||
                    Objects.equals(fr.getId().getRight(), entity.getId())) {
                this.deleteFriendship(fr);
            }
        }
        this.notifyObservers(UpdateType.USERS,
                new UserEvent(deleted, OperationType.DELETE));
        return deleted;
    }

    /**
     * update logged-in user
     * @param entity user
     * @param firstName new first name
     * @param lastName new last name
     * @return old state of entity
     * @throws RepositoryException if user was not found
     */
    @Override
    public User updateUser(User entity, String firstName, String lastName){
        entity.setFirstName(firstName);
        entity.setLastName(lastName);
        User oldState = userService.update(entity);
        if(oldState == null)
            throw new RepositoryException("User doesn't exist!\n");
        entity.setId(oldState.getId());
        this.notifyObservers(UpdateType.USERS,
                new UserEvent(entity, OperationType.UPDATE));
        return oldState;
    }

    /**
     * find user by given id
     * @param id Long
     * @return user if found
     * @throws RepositoryException if user was not found
     */
    @Override
    public User findUserById(Long id) {
        User user = userService.findOneById(id);
        if(user == null)
            throw new RepositoryException("User with id = " + id + " doesn't exist!\n");
        return user;
    }

    /**
     * find user by given username
     * @param username String
     * @return user if found
     * @throws RepositoryException if user was not found
     */
    @Override
    public User findUserByUsername(String username) {
        if(username == null || username.equals(""))
            throw new RepositoryException("Username empty!\n");
        User user = userService.findOneByUsername(username);
        if(user == null)
            throw new RepositoryException("User with username = " + username + " doesn't exist!\n");
        return user;
    }

    /**
     * get iterable list of all existing users
     * @return iterable of users
     */
    @Override
    public Iterable<User> getAllUsers() {
        return userService.getAllEntities();
    }

    /**
     * add a friendship
     * @param entity friendship composed of 2 user IDs
     * @throws RepositoryException if either ID doesn't match an user
     * @throws RepositoryException if friendship between the 2 users already
     * exists
     */
    @Override
    public void addFriendship(Friendship entity) {
        Long id1 = entity.getId().getLeft();
        User user1 = userService.findOneById(id1);
        Long id2 = entity.getId().getRight();
        User user2 = userService.findOneById(id2);
        if(user1 == null || user2 == null)
            throw new RepositoryException("User(s) doesn't exist!\n");
        Friendship exists = friendshipService.add(entity);
        if(exists != null)
            throw new RepositoryException("Friendship already exists!\n");
        this.notifyObservers(UpdateType.FRIENDS,
                new FriendshipEvent(entity, OperationType.ADD));
    }

    /**
     * delete friendship between 2 users, by given IDs
     * @param entity friendship composed of 2 user IDs
     * @return old state of friendship
     * @throws RepositoryException if either ID doesn't match an user
     * @throws RepositoryException if friendship between the 2 users doesn't
     * exist
     */
    @Override
    public Friendship deleteFriendship(Friendship entity) {
        Long id1 = entity.getId().getLeft();
        User user1 = userService.findOneById(id1);
        Long id2 = entity.getId().getRight();
        User user2 = userService.findOneById(id2);
        if(user1 == null || user2 == null)
            throw new RepositoryException("User(s) doesn't exist!\n");
        Friendship fr = friendshipService.delete(entity);
        if(fr == null)
            throw new RepositoryException("Friendship doesn't exist!\n");
        this.notifyObservers(UpdateType.FRIENDS,
                new FriendshipEvent(entity, OperationType.DELETE));
        return fr;
    }

    /**
     * update state of friendship between 2 users, by given IDs
     * @param entity friendship composed of 2 IDs
     * @return old state of friendship
     * @throws RepositoryException if friendship between the 2 users doesn't
     * exist
     */
    @Override
    public Friendship updateFriendship(Friendship entity){
        Friendship oldState = friendshipService.update(entity);
        if(oldState == null)
            throw new RepositoryException("Friendship doesn't exist!\n");
        this.notifyObservers(UpdateType.FRIENDS,
                new FriendshipEvent(entity, OperationType.UPDATE));
        return oldState;
    }

    /**
     *  get an iterable list of all friendships existing
     * @return iterable of friendships
     */
    @Override
    public Iterable<Friendship> getAllFriendships() {
        return friendshipService.getAllEntities();
    }

    @Override
    public List<User> getAllFriends(User user) {
        List<User> friends = new ArrayList<>();
        Tuple<Long,Long> id = new Tuple<>(user.getId(),0L);
        Iterable<Friendship> friendships = friendshipService.getFriendships(id);
        for(Friendship fr : friendships) {
            Long friendId = fr.getId().getLeft().equals(user.getId())
                            ? fr.getId().getRight()
                            : fr.getId().getLeft();
            User friend = userService.findOneById(friendId);
            if(friend != null)
                friends.add(friend);
        }
        return friends;
    }

    /**
     * draw graph of all existing friendships
     */
    private void runGraph(){
        graph = new Graph(this);
        graph.runConnectedComponents();
    }

    /**
     * get list of all communities existing. community = connected component
     * of users
     * @return list of IDs list
     */
    @Override
    public ArrayList<ArrayList<Long>> getAllCommunities(){
        this.runGraph();
        return graph.getCommunities();
    }

    /**
     * get size of biggest existing community
     * @return size
     */
    @Override
    public int getBiggestCommunitySize(){
        this.runGraph();
        return graph.maxSize();
    }

    @Override
    public Stream<FriendshipDTO> getRightFriends(User user, List<Friendship> friendshipList) {
        Predicate<Friendship> friends = x -> x.getId().getLeft().equals(user.getId());
        return friendshipList.stream().filter(friends).map(x ->
                new FriendshipDTO(findUserById(x.getId().getRight()).getLastName(),
                        findUserById(x.getId().getRight()).getFirstName(), x.getDate()));
    }

    @Override
    public Stream<FriendshipDTO> getLeftFriends(User user, List<Friendship> friendshipList) {
        Predicate<Friendship> friends = x -> x.getId().getRight().equals(user.getId());
        return friendshipList.stream().filter(friends).map(x ->
                new FriendshipDTO(findUserById(x.getId().getLeft()).getLastName(),
                        findUserById(x.getId().getLeft()).getFirstName(), x.getDate()));
    }

    @Override
    public Stream<FriendshipDTO> getRightFriendsMonth(User user, Month month, List<Friendship> friendshipList) {
        Predicate<Friendship> friends = x -> x.getId().getLeft().equals(user.getId());
        Predicate<Friendship> friendsMonth = x -> x.getDate().getMonth() == month;
        Predicate<Friendship> filtered = friends.and(friendsMonth);
        return friendshipList.stream().filter(filtered).map(x ->
                new FriendshipDTO(findUserById(x.getId().getRight()).getLastName(),
                        findUserById(x.getId().getRight()).getFirstName(), x.getDate()));
    }

    @Override
    public Stream<FriendshipDTO> getLeftFriendsMonth(User user, Month month, List<Friendship> friendshipList) {
        Predicate<Friendship> friends = x -> x.getId().getRight().equals(user.getId());
        Predicate<Friendship> friendsMonth = x -> x.getDate().getMonth() == month;
        Predicate<Friendship> filtered = friends.and(friendsMonth);
        return friendshipList.stream().filter(filtered).map(x ->
                new FriendshipDTO(findUserById(x.getId().getLeft()).getLastName(),
                        findUserById(x.getId().getLeft()).getFirstName(), x.getDate()));
    }



    @Override
    public void sendMessage(User source, List<String> destinationUsernames, String message, LocalDateTime date, Long repliedMessageId) {
        List<User> destination = new ArrayList<>();
        for(String s : destinationUsernames){
            User user = this.findUserByUsername(s);
            destination.add(user);
        }
        Message repliedMsg = messageService.findMessageById(repliedMessageId);
        Message msg = new Message(source,destination, message, date, repliedMsg);
        if(repliedMsg != null)
            setupMessage(msg);
        msg = this.messageService.add(msg);
        this.notifyObservers(UpdateType.MESSAGES,
                new MessageEvent(msg, OperationType.ADD));
    }

    private void setupMessage(Message msg){
        User user = this.findUserById(msg.getSource().getId());
        msg.setSource(user);
        List<User> destinationUsers = new ArrayList<>();
        for(User dest : msg.getDestination()){
            try{
                destinationUsers.add(this.findUserById(dest.getId()));
            }catch(RepositoryException e){
                System.out.println("#[Controller]" + e.getMessage());
            }
        }
        msg.setDestination(destinationUsers);
        Message repliedMessage = msg.getRepliedMessage();
        if(repliedMessage != null){
            repliedMessage = messageService.findMessageById(repliedMessage.getId());
            User rUser = this.findUserById(repliedMessage.getSource().getId());
            repliedMessage.setSource(rUser);
            msg.setRepliedMessage(repliedMessage);
        }
    }

    private void setupMessage(GroupMessage msg) {
        User user = this.findUserById(msg.getSource().getId());
        msg.setSource(user);
        GroupMessage repliedMessage = msg.getRepliedMessage();
        if(repliedMessage != null) {
            repliedMessage = messageService.findGroupMessageById(repliedMessage.getId());
            User rUser = this.findUserById(repliedMessage.getSource().getId());
            repliedMessage.setSource(rUser);
            msg.setRepliedMessage(repliedMessage);
        }
    }

    @Override
    public Iterable<Message> getAllMesagesOfUser(String username) {
        User source = this.userService.findOneByUsername(username);
        Iterable<Message> messages = this.messageService.findAllMessagesBySource(source.getId());
        for(Message m : messages){
            setupMessage(m);
        }
        return messages;
    }

    @Override
    public Iterable<Message> getConversation(String username1, String username2) {
        User user1 = this.findUserByUsername(username1);
        Long id1 = user1.getId();
        User user2 = this.findUserByUsername(username2);
        Long id2 = user2.getId();
        Set<Message> messages = this.messageService.findConversation(id1,id2);
        for(Message m : messages){
            setupMessage(m);
        }
        return messages;
    }

    @Override
    public Iterable<GroupMessage> getGroupConversation(User user, String nameGroup) {
        Group group = this.findGroupByName(nameGroup, user);
        Set<GroupMessage> messages = this.messageService.findGroupConversation(group.getId());
        for(GroupMessage m : messages) {
            setupMessage(m);
        }
        return messages;
    }

    @Override
    public void sendGroupMessage(User source, Long idGroup, String message, LocalDateTime date) {
        if(this.groupService.findGroupById(idGroup) == null)
            throw new RepositoryException("Group not found!");
        GroupMessage msg = new GroupMessage(source, idGroup, message, date);
        msg = this.messageService.add(msg);
        this.notifyObservers(UpdateType.GROUPMESSAGES, new GroupMessageEvent(msg, OperationType.ADD));
    }

    @Override
    public void sendGroupMessage(User source, Long idGroup, String message, LocalDateTime date, Long repliedMessageId) {
        if(this.groupService.findGroupById(idGroup) == null)
            throw new RepositoryException("Group not found!");
        GroupMessage repliedMsg = messageService.findGroupMessageById(repliedMessageId);
        GroupMessage msg = new GroupMessage(source, idGroup, message, date, repliedMsg);
        setupMessage(msg);
        msg = this.messageService.add(msg);
        this.notifyObservers(UpdateType.GROUPMESSAGES, new GroupMessageEvent(msg, OperationType.ADD));
    }

    @Override
    public void createGroup(String nameGroup, List<User> users) {
        if( nameGroup != null && !Objects.equals(nameGroup, "")){
            Set<Long> idsList = new HashSet<>();
            users.forEach( u -> idsList.add(u.getId()));
            Group entity = new Group(nameGroup, idsList.stream().toList());
            Group created = this.groupService.add(entity);
            this.notifyObservers(UpdateType.GROUPS,
                    new GroupEvent(created, OperationType.ADD));
        }
    }

    @Override
    public void addMemberToGroup(Group group, User user) {
        this.groupService.addMemberToGroup(group, user);
        this.notifyObservers(UpdateType.GROUPS, new GroupEvent(group, OperationType.UPDATE));
    }

    @Override
    public Group findGroupByName(String name, User user) {
        Group group = this.groupService.findGroupByName(name, user);
        if(group == null)
            throw new RepositoryException("Group not found!");
        return group;
    }

    @Override
    public Iterable<Group> getAllGroups(User user) {
        return this.groupService.findAll(user.getId());
    }



    @Override
    public void addRequest(Request request) {
        Request found = requestService.findOneById(request.getId());
        if (found != null) {
                throw new RepositoryException("Friendship request already sent!");
        }
        requestService.add(request);
    }

    private void validateAnswer(String answer) {
        if(!answer.equals("approve") && !answer.equals("reject")) {
            throw new RepositoryException("Invalid answer!");
        }
    }

    @Override
    public void answerRequest(Request request, String answer) {
        validateAnswer(answer);
        Request found = requestService.findOneById(request.getId());
        if(found == null) {
            throw new RepositoryException("Request does not exist!");
        }
        if(found.getStatus().equals("approve")) {
            throw new RepositoryException("Request already approved!");
        }
        if(found.getStatus().equals("reject")) {
            throw new RepositoryException("Request already rejected!");
        }
        Request newRequest = new Request(found.getId().getLeft(), found.getId().getRight(), answer, found.getDate());
        requestService.update(newRequest);
        System.out.println(newRequest);
        if(answer.equals("approve")) {
            Friendship friendship = new Friendship(request.getId().getLeft(), request.getId().getRight());
            this.addFriendship(friendship);
        }
        this.notifyObservers(UpdateType.REQUESTS,
                new RequestEvent(newRequest, OperationType.DELETE));
        this.notifyObservers(UpdateType.SOLVEDREQUESTS,
                new RequestEvent(newRequest, OperationType.ADD));
    }

    @Override
    public Iterable<Request> getAllRequests(User user) {
        Iterable<Request> requests = requestService.getAllEntities();
        ArrayList<Request> requestsToUser = new ArrayList<>();
        for(Request request : requests) {
            if(request.getId().getRight().equals(user.getId()) && request.getStatus().equals("pending")) {
                requestsToUser.add(request);
            }
        }
        return requestsToUser;
    }

    @Override
    public Iterable<Request> getAllAnsweredRequests(User user) {
        Iterable<Request> requests = requestService.getAllEntities();
        ArrayList<Request> requestsToUser = new ArrayList<>();
        for(Request request : requests) {
            if(request.getId().getRight().equals(user.getId()) && !request.getStatus().equals("pending")) {
                requestsToUser.add(request);
            }
        }
        return requestsToUser;
    }

    @Override
    public Request findRequest(Request request) {
        Iterable<Request> requests = requestService.getAllEntities();
        for(Request request1 : requests) {
            if(request.equals(request1))
                return request1;
        }
        return null;
        //throw new RepositoryException("Request not found!");
    }

    @Override
   public Request deleteRequest(Request request) {
        Request re = requestService.delete(request);
        if(re == null)
            throw new RepositoryException("Request doesn't exist!\n");
        this.notifyObservers(UpdateType.REQUESTS,
                new RequestEvent(request, OperationType.DELETE));
        return re;
    }

    @Override
    public Iterable<Request> showAllRequests() {
        return requestService.getAllEntities();
    }

    @Override
    public Friendship findFriendshipById(Tuple<Long,Long> id) {
        return friendshipService.findFriendshipById(id);
    }

    @Override
    public Iterable<SocialEvent> showAllEvents() {
        return eventService.findAll();
    }

    @Override
    public Iterable<SocialEvent> showAllEventsOfUser(User user) {
        return eventService.findAll(user.getId());
    }

    @Override
    public Iterable<SocialEvent> showAllEventsByName(String name) {
        return eventService.findAll(name);
    }

    @Override
    public SocialEvent createEvent(SocialEvent event) {
        event = eventService.save(event);
        SocialEventEvent ev = new SocialEventEvent(event,OperationType.ADD);
        this.notifyObservers(UpdateType.SOCIALEVENTS, ev);
        return event;
    }

    @Override
    public SocialEvent delete(SocialEvent event) {
        return eventService.delete(event.getId());
    }

    @Override
    public void addParticipantToEvent(SocialEvent event, User user) {
        if(eventService.findParticipantInEvent(event.getId(), user.getId()))
            //throw new RepositoryException("User is already participating in this event");
            return;
        eventService.addParticipant(event.getId(), user.getId(), 1);
        event = eventService.findOneById(event.getId());
        this.notifyObservers(UpdateType.SOCIALEVENTS,
                new SocialEventEvent(event, OperationType.UPDATE));
    }

    @Override
    public void removeParticipantFromEvent(SocialEvent event, User user) {
        if(!eventService.findParticipantInEvent(event.getId(), user.getId()))
           // throw new RepositoryException("User is not participating in this event");
            return;
        eventService.removeParticipant(event.getId(), user.getId());
        event = eventService.findOneById(event.getId());
        this.notifyObservers(UpdateType.SOCIALEVENTS,
                new SocialEventEvent(event, OperationType.UPDATE));
    }

    @Override
    public void addNotification(SocialEvent event, User user) {
        if(!eventService.findParticipantInEvent(event.getId(), user.getId()))
            throw new RepositoryException("User is not participating in this event");
        eventService.addNotification(event.getId(), user.getId());
    }

    @Override
    public void removeNotification(SocialEvent event, User user) {
        if(!eventService.findParticipantInEvent(event.getId(), user.getId()))
            throw new RepositoryException("User is not participating in this event");
        eventService.removeNotification(event.getId(), user.getId());
    }

    @Override
    public boolean findNotificationOfParticipant(SocialEvent event, User user) {
        return eventService.findNotificationOfParticipant(event.getId(), user.getId());
    }


    private final List<Observer> observers = new ArrayList<>();

    @Override
    public void notifyObservers(UpdateType updateType, Event event) {
        System.out.println("Observable notifying " + updateType + " for " +event.getOperationType());
        for (Observer observer : this.observers) {
            if(updateType == UpdateType.USERS){
                observer.updateUsers(event);
            }
            if(updateType == UpdateType.FRIENDS){
                observer.updateFriends(event);
            }
            if(updateType == UpdateType.REQUESTS){
                observer.updateRequests(event);
            }
            if(updateType == UpdateType.MESSAGES){
                observer.updateMessages(event);
            }
            if(updateType == UpdateType.GROUPMESSAGES){
                observer.updateGroupMessages(event);
            }
            if(updateType == UpdateType.SOLVEDREQUESTS){
                observer.updateSolvedRequests(event);
            }
            if(updateType == UpdateType.GROUPS) {
                observer.updateGroups(event);
            }
            if(updateType == UpdateType.SOCIALEVENTS) {
                observer.updateEvents(event);
            }
        }
    }

    @Override
    public void addObserver(Observer obs) {
        this.observers.add(obs);
    }

    @Override
    public void removeObserver(Observer obs) {
        this.observers.remove(obs);
    }

    @Override
    public Tuple<String,String> generatePassword(String password) {
        try{
            SecureRandom random = new SecureRandom();

            int randomInt = random.nextInt();
            String salt = Integer.toString(randomInt);
            String p = password + salt;

            MessageDigest digest = MessageDigest.getInstance("SHA-256");


            byte[] hash = digest.digest(p.getBytes(StandardCharsets.UTF_8));
            BigInteger no = new BigInteger(1, hash);
            String hashtext = no.toString(16);

            return new Tuple<>(hashtext, salt);
        } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
        }
        return null;
    }

    @Override
    public String hashCodePassword(String username, String password) {
        try{
            String salt = this.userService.getSalt(username);
            String p = password + salt;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(p.getBytes(StandardCharsets.UTF_8));
            BigInteger no = new BigInteger(1, hash);
            return no.toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveMessageReportToPDF(String path, String fileName, LocalDate startDate, LocalDate endDate, User user) {
        if ( fileName.equals("") || path.equals(""))
            return;
        messageService.setPageSize(10);
        PDDocument doc = null;
        try {
            doc = new PDDocument();
            PDFont pdfFont = PDType1Font.TIMES_ROMAN;
            float fontSize = 12;
            float leading = 1.5f * fontSize;
            Set<Message> msgs = messageService.getNextMessages(user.getId());
            int i = 0;
            while(msgs.size() != 0) {
                PDPage page = new PDPage();
                doc.addPage(page);
                PDPageContentStream contentStream = new PDPageContentStream(doc, page);
                PDRectangle mediabox = page.getMediaBox();
                float margin = 16;
                float width = mediabox.getWidth() - 2*margin;
                float startX = mediabox.getLowerLeftX() + margin;
                float startY = mediabox.getUpperRightY() - margin;
                contentStream.beginText();
                contentStream.setFont(pdfFont, fontSize);
                contentStream.newLineAtOffset(startX, startY);
                System.out.println("contentStream start");
                System.out.println("printing page " + i);
                i++;
                for(Message m : msgs) {
                    setupMessage(m);
                    System.out.println(m);
                    LocalDate date = m.getDate().toLocalDate();
                    if( !(date.isAfter(startDate) && date.isBefore(endDate)) )
                        continue;
                    List<String> strs = Arrays.stream(
                            m.toString().replace("\t", "").split("\n"))
                            .toList();
                    for(String text : strs) {
                        List<String> lines = new ArrayList<>();
                        int lastSpace = -1;
                        while (text.length() > 0)
                        {
                            int spaceIndex = text.indexOf(' ', lastSpace + 1);
                            if (spaceIndex < 0)
                                spaceIndex = text.length();
                            String subString = text.substring(0, spaceIndex);
                            float size = fontSize * pdfFont.getStringWidth(subString) / 1000;
                            System.out.printf("'%s' - %f of %f\n", subString, size, width);
                            if (size > width)
                            {
                                if (lastSpace < 0)
                                    lastSpace = spaceIndex;
                                subString = text.substring(0, lastSpace);
                                lines.add(subString);
                                text = text.substring(lastSpace).trim();
                                System.out.printf("'%s' is line\n", subString);
                                lastSpace = -1;
                            }
                            else if (spaceIndex == text.length())
                            {
                                lines.add(text);
                                System.out.printf("'%s' is line\n", text);
                                text = "";
                            }
                            else
                            {
                                lastSpace = spaceIndex;
                            }
                        }
                        for (String line: lines)
                        {
                            System.out.println(line);
                            contentStream.showText(line);
                            contentStream.newLineAtOffset(0, -leading);
                        }
                    }
                }
                contentStream.endText();
                contentStream.close();
                System.out.println("contentStream closed");
                msgs = messageService.getNextMessages(user.getId());
            }
            System.out.println("saving");
            doc.save(new File(path, fileName));
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            messageService.setPage(-1);
            messageService.setPageSize(1);
            if (doc != null) {
                try {
                    doc.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
