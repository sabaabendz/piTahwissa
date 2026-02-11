<?php

namespace App\Form;

use App\Entity\Collaborator;
use App\Repository\ManagerRepository;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\CheckboxType;
use Symfony\Component\Form\Extension\Core\Type\EmailType;
use Symfony\Component\Form\Extension\Core\Type\PasswordType;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Validator\Constraints\Callback;
use Symfony\Component\Validator\Constraints\IsTrue;
use Symfony\Component\Validator\Constraints\Length;
use Symfony\Component\Validator\Constraints\NotBlank;
use Symfony\Component\Validator\Context\ExecutionContextInterface;

class CollaboratorType extends AbstractType
{
    public function __construct(private ManagerRepository $managerRepository)
    {
    }

    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $isEdit = $options['is_edit'] ?? false;

        $builder
            ->add('name', TextType::class, [
                'attr' => ['class' => 'form-control'],
                'label_attr' => ['class' => 'form-label'],
            ])
            ->add('email', EmailType::class, [
                'attr' => ['class' => 'form-control'],
                'label_attr' => ['class' => 'form-label'],
            ])
            ->add('password', PasswordType::class, [
                'mapped' => false,
                'required' => !$isEdit,
                'attr' => [
                    'class' => 'form-control',
                    'autocomplete' => 'new-password',
                ],
                'label_attr' => ['class' => 'form-label'],
                'constraints' => $isEdit ? [] : [
                    new NotBlank([
                        'message' => 'Please enter a password',
                    ]),
                    // Keeping minimal constraints on unmapped password field as explained in ManagerType
                    new Length([
                        'min' => 8,
                        'minMessage' => 'Your password should be at least {{ limit }} characters',
                        'max' => 4096,
                    ]),
                ],
                'help' => $isEdit ? 'Leave blank to keep current password' : 'Minimum 8 characters',
            ])
            ->add('post', TextType::class, [
                'attr' => ['class' => 'form-control'],
                'label_attr' => ['class' => 'form-label'],
            ])
            ->add('team', TextType::class, [
                'attr' => ['class' => 'form-control'],
                'label_attr' => ['class' => 'form-label'],
            ])
            ->add('enterpriseCode', TextType::class, [
                'attr' => [
                    'class' => 'form-control',
                    'placeholder' => 'Enter enterprise code',
                ],
                'label_attr' => ['class' => 'form-label'],
                'constraints' => [
                    new Callback([$this, 'validateEnterpriseCode']),
                ],
            ]);

        if (!$isEdit) {
            $builder->add('terms', CheckboxType::class, [
                'mapped' => false,
                'constraints' => [
                    new IsTrue([
                        'message' => 'You must agree to the terms of service.',
                    ]),
                ],
                'label' => 'I agree to the terms of service',
                'attr' => ['class' => 'form-check-input'],
                'label_attr' => ['class' => 'form-check-label'],
            ]);
        }
    }

    public function validateEnterpriseCode($object, ExecutionContextInterface $context): void
    {
        // $object is the value of the field? No, for Callback on field, it is the value.
        // Wait, Callback constraint on a field receives the value as first arg?
        // Let's verify. Yes.
        
        $enterpriseCode = $object;
        if (!$enterpriseCode) {
            return; // NotBlank handles null/empty
        }

        $manager = $this->managerRepository->findOneBy(['enterpriseCode' => $enterpriseCode]);
        if (!$manager) {
            $context->buildViolation('Invalid enterprise code. Please check with your manager.')
                ->addViolation();
        }
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Collaborator::class,
            'is_edit' => false,
        ]);
    }
}
