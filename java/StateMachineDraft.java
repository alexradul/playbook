import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;

class Scratch {

    public static void main(String[] args) {
        TransactionStateMachine stateMachine = new TransactionStateMachine();
        Transaction trx = new Transaction();

        TransactionTransition transition = new AssignTransition();

        stateMachine.execute(trx, transition);

    }
}


// treba mi jedan enum za stanja objekta: TransactionState
// treba mi jedan enum za nazive/vrste tranzicija: TransactionTransitionKind ili TransactionTransitionName
// jedna klasa da sadrzi podatke o tranziciji: source (state) - transitionName - target (state)
// builder za tranzicije

// cela klasna hijerarhija za tranzicije:
// TransactionStateTransition

// ako imam celu klasnu hijerarhiju za tranzicije, tj. po jednu klasu za svaku vrstu tranzicije, onda mi mozda meta-model
// i nije potreban. Mozda mogu jednostavno da u ovom objektu navedem informaciju o početnom i završnom stanju, kao i o nazivu
// tranzicije?

abstract class StateMachine<H> {

    public <T extends StateMachine<H>> void execute(H holder, StateTransition<H, T> transition) {
        transition.perform(holder,getThis());
    }

    abstract <T extends StateMachine<H>> T getThis();
}
abstract class StateTransition<H, M extends StateMachine<H>> {

    public final void perform(H stateHolder, M stateMachine) {
        checkPreconditions(stateHolder);
        execute(stateMachine, stateHolder);
        changeState(stateHolder);
    }

    protected abstract void checkPreconditions(H stateHolder);

    protected abstract void execute(M stateMachine, H stateHolder);

    protected abstract void changeState(H stateHolder);

}

class Transaction{
    private TransactionState transactionState;

    public TransactionState getTransactionState() {
        return transactionState;
    }

    public Transaction setTransactionState(TransactionState transactionState) {
        this.transactionState = transactionState;
        return this;
    }
}

enum TransactionState {
    unassigned,
    assigned,
    sent,
    exported,
    processed
}

enum TransactionTransitionName {
    assign,
    send,
    export,
    process
}

@Service
class TransactionStateMachine extends StateMachine<Transaction> {
    /** @noinspection unchecked*/
    @Override
    TransactionStateMachine getThis() {
        return this;
    }

    public void assign(Transaction stateHolder, String accountNo) {
        System.out.println("Assigning account no " + accountNo + " to " + stateHolder );
    }
}

abstract class TransactionTransition extends StateTransition<Transaction, TransactionStateMachine> {
    private static final Logger logger = LoggerFactory.getLogger(TransactionTransition.class);

    private final TransactionState source;
    private final TransactionTransitionName transitionName;
    private final TransactionState target;

    TransactionTransition(TransactionState source, TransactionTransitionName transitionName, TransactionState target) {
        Objects.requireNonNull(source, "Source transaction state must not be null");
        Objects.requireNonNull(transitionName, "Transaction transition name must not be null");
        Objects.requireNonNull(target, "Target transaction state must not be null");
        this.source = source;
        this.transitionName = transitionName;
        this.target = target;
    }

    @Override
    protected void checkPreconditions(Transaction trx) {
        if (!source.equals(trx.getTransactionState())) {
            throw new IllegalArgumentException(String.format("Transition %s is not applicable to transaction %s", this, trx));
        }
    }

    @Override
    protected void changeState(Transaction trx) {
        trx.setTransactionState(target);
    }
}

class AssignTransition extends TransactionTransition {
    private String accountNo;

    public AssignTransition() {
        super(TransactionState.unassigned, TransactionTransitionName.assign, TransactionState.assigned);
    }

    @Override
    protected void checkPreconditions(Transaction stateHolder) {
        super.checkPreconditions(stateHolder);
        if (StringUtils.isBlank(accountNo)) {
            throw new IllegalArgumentException("Account number must not be null when assigning account number to transaction: "+ stateHolder);
        }
    }

    @Override
    protected void execute(TransactionStateMachine stateMachine, Transaction stateHolder) {
        stateMachine.assign(stateHolder, accountNo);
    }
}

class Report {
    private ReportState state;

    public ReportState getState() {
        return state;
    }

    public Report setState(ReportState state) {
        this.state = state;
        return this;
    }
}

enum ReportState {
    requested,
    pending,
    created,
    downloaded
}

enum ReportTransitionName {
    request,
    makePending,
    create,
    download
}

class ReportStateMachine extends StateMachine<Report> {
    /** @noinspection unchecked*/
    @Override
    ReportStateMachine getThis() {
        return this;
    }
}

class ReportStateTransition extends StateTransition<Report, ReportStateMachine> {

    @Override
    protected void checkPreconditions(Report stateHolder) {

    }

    @Override
    protected void execute(ReportStateMachine stateMachine, Report stateHolder) {

    }

    @Override
    protected void changeState(Report stateHolder) {

    }
}


class TripRequest {
    private TripRequestState state;

    public TripRequestState getState() {
        return state;
    }

    public TripRequest setState(TripRequestState state) {
        this.state = state;
        return this;
    }
}

enum TripRequestState {
    waitingForConfirmation,
    confirmed,
    processing,
    completed,
    canceled
}

enum TripRequestTransitionName {
    waitForConfirmation,
    confirm,
    process,
    complete,
    cancel
}

class TripRequestTransition extends StateTransition<TripRequest, TripRequestStateMachine> {

    @Override
    protected void checkPreconditions(TripRequest stateHolder) {

    }

    @Override
    protected void execute(TripRequestStateMachine stateMachine, TripRequest stateHolder) {

    }

    @Override
    protected void changeState(TripRequest stateHolder) {

    }
}

class ConfirmTransition extends TripRequestTransition {
    @Override
    protected void execute(TripRequestStateMachine stateMachine, TripRequest stateHolder) {
        stateMachine.confirm(stateHolder);
    }
}

@Service
class TripRequestStateMachine extends StateMachine<TripRequest> {

    /** @noinspection unchecked*/
    @Override
    TripRequestStateMachine getThis() {
        return this;
    }

    public void confirm(TripRequest request) {
    }
}
